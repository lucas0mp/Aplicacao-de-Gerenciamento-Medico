package dao;

import model.Prescricao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.CallableStatement; // Importe este
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException; // Importe este
import java.text.SimpleDateFormat; // Importe este
import java.util.List;
import java.util.Map;

@Repository
public class MedicoDAO {

    // REQUISITO: Usa a conexão de Médico
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MedicoDAO(@Qualifier("medicoDataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    
    // Mapeador para converter SQL em Objeto Paciente (simplificado)
    private static final class PacienteRowMapper implements RowMapper<Map<String, Object>> {
        @Override
        public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Map.of(
                "id_paciente", rs.getInt("id_paciente"),
                "nome_paciente", rs.getString("nome_paciente"),
                "cpf_paciente", rs.getString("cpf_paciente"),
                "email_paciente", rs.getString("email_paciente")
            );
        }
    }

    /**
     * REQUISITO: Lê da VIEW 'vw_medico_paciente'
     */
    public List<Map<String, Object>> getMeusPacientes(int idMedico) {
        String sql = "SELECT * FROM vw_medico_paciente WHERE id_medico = ?";
        return jdbcTemplate.query(sql, new PacienteRowMapper(), idMedico);
    }

    /**
     * REQUISITO: Chama a PROCEDURE 'sp_registrar_medicao_glicemia'
     */
    public void registrarGlicemia(int idPaciente, double nivel, String periodo, String obs) {
        String sql = "{CALL sp_registrar_medicao_glicemia(?, ?, ?, ?)}";
        jdbcTemplate.update(sql, idPaciente, nivel, periodo, obs);
    }

    /**
     * Registra medição de pressão (lógica de transação manual)
     */
    public void registrarPressao(int idPaciente, double sistolica, double diastolica, String obs) {
         // O 'app_medico' tem permissão de INSERT em 'medicao' e 'medicao_pressao'
        String sqlBase = "INSERT INTO medicao (id_paciente, data_hora, observacoes) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlBase, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, idPaciente);
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            ps.setString(3, obs);
            return ps;
        }, keyHolder);
        
        int idMedicao = keyHolder.getKey().intValue();

        String sqlPressao = "INSERT INTO medicao_pressao (id_medicao, pressao_sistolica, pressao_diastolica) VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlPressao, idMedicao, sistolica, diastolica);
    }

    /**
     * Cria uma Prescrição e um Lembrete associado.
     */
    public void criarPrescricao(Prescricao p, java.util.Date horarioLembrete) {
        // O 'app_medico' tem permissão de INSERT em 'prescricao' e 'lembrete'
        String sqlPrescricao = "INSERT INTO prescricao (id_medico, id_paciente, id_medicamento, data_inicio, dosagem, frequencia) VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlPrescricao, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, p.getId_medico());
            ps.setInt(2, p.getId_paciente());
            ps.setInt(3, p.getId_medicamento());
            ps.setDate(4, new Date(p.getData_inicio().getTime()));
            ps.setString(5, p.getDosagem());
            ps.setString(6, p.getFrequencia());
            return ps;
        }, keyHolder);
        
        int idPrescricao = keyHolder.getKey().intValue();

        String sqlLembrete = "INSERT INTO lembrete (id_prescricao, horario_programado, status) VALUES (?, ?, 'Pendente')";
        jdbcTemplate.update(sqlLembrete, idPrescricao, new Timestamp(horarioLembrete.getTime()));
    }

    /**
     * (NOVO MÉTODO) Cria um paciente.
     * REQUISITO: O 'app_medico' chama a PROCEDURE 'sp_registrar_novo_paciente'
     * (Inspirado no `criarPaciente` do `AdminDAO` e no `MedicoController` do POO)
     */
    public void criarPaciente(Map<String, String> dados, int idMedicoResponsavel) throws Exception {
        String sql = "{CALL sp_registrar_novo_paciente(?, ?, ?, ?, ?, ?, ?)}";
        
        // Usa a conexão 'app_medico', que tem permissão
        jdbcTemplate.execute(sql, (CallableStatement cs) -> {
            try {
                cs.setString(1, dados.get("nome"));
                cs.setString(2, dados.get("cpf"));
                cs.setString(3, dados.get("email"));
                
                SimpleDateFormat formatador = new SimpleDateFormat("yyyy-MM-dd"); 
                java.util.Date dataNasc = formatador.parse(dados.get("data_nascimento"));
                cs.setDate(4, new Date(dataNasc.getTime()));
                
                cs.setString(5, dados.get("telefone_celular"));
                cs.setString(6, dados.get("senha"));
                cs.setInt(7, idMedicoResponsavel); // ID do médico logado
                cs.execute();
            } catch (ParseException e) {
                throw new SQLException("Formato de data inválido. Use AAAA-MM-DD.", e);
            }
            return null;
        });
    }
}