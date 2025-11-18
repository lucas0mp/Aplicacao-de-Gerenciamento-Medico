package dao;

import model.Medicamento; 
import model.Paciente;
import model.Prescricao; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper; 
import org.springframework.jdbc.support.GeneratedKeyHolder; // Importação Adicionada
import org.springframework.jdbc.support.KeyHolder; // Importação Adicionada
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.PreparedStatement; // Importação Adicionada
import java.sql.ResultSet; 
import java.sql.SQLException;
import java.sql.Statement; // Importação Adicionada
import java.sql.Timestamp; // Importação Adicionada
import java.text.ParseException; 
import java.text.SimpleDateFormat; 
import java.util.List;
import java.util.Map;

@Repository
public class MedicoDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MedicoDAO(@Qualifier("medicoDataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // --- Métodos de Paciente ---

    public List<Map<String, Object>> getPacientesPorMedico(int idMedico) {
        String sql = "SELECT * FROM vw_medico_paciente WHERE id_medico = ?";
        return jdbcTemplate.queryForList(sql, idMedico);
    }

    public void criarPacienteMedico(Map<String, String> dados) throws Exception {
        String sql = "{CALL sp_registrar_novo_paciente(?, ?, ?, ?, ?, ?, ?)}";
        
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
                cs.setInt(7, Integer.parseInt(dados.get("id_medico_responsavel")));
                cs.execute();
            } catch (ParseException e) {
                throw new SQLException("Formato de data inválido. Use AAAA-MM-DD.", e);
            }
            return null;
        });
    }

    public void atualizarPaciente(int idPaciente, Paciente paciente) {
        String sql = "UPDATE paciente SET nome = ?, data_nascimento = ?, email = ?, telefone_celular = ? WHERE id_paciente = ?";
        jdbcTemplate.update(sql, paciente.getNome(), paciente.getData_nascimento(), paciente.getEmail(), paciente.getTelefone_celular(), idPaciente);
    }

    public void deletarPaciente(int idPaciente) {
        String sql = "DELETE FROM paciente WHERE id_paciente = ?";
        jdbcTemplate.update(sql, idPaciente);
    }

    // --- Métodos de Medição ---

    public void registrarGlicemia(Map<String, String> dados) throws SQLException {
        String sql = "{CALL sp_registrar_medicao_glicemia(?, ?, ?, ?)}";
        
        jdbcTemplate.execute(sql, (CallableStatement cs) -> {
            cs.setInt(1, Integer.parseInt(dados.get("idPaciente")));
            cs.setDouble(2, Double.parseDouble(dados.get("nivel")));
            cs.setString(3, dados.get("periodo"));
            cs.setString(4, dados.get("obs")); 
            cs.execute();
            return null;
        });
    }
    
    // --- Métodos de Prescrição (MODIFICADO) ---

    private static final class MedicamentoRowMapper implements RowMapper<Medicamento> {
        @Override
        public Medicamento mapRow(ResultSet rs, int rowNum) throws SQLException {
            Medicamento med = new Medicamento();
            med.setId_medicamento(rs.getInt("id_medicamento"));
            med.setNome_comercial(rs.getString("nome_comercial"));
            med.setPrincipio_ativo(rs.getString("principio_ativo"));
            return med;
        }
    }
    
    public List<Medicamento> getTodosMedicamentos() {
        String sql = "SELECT * FROM medicamento ORDER BY nome_comercial";
        return jdbcTemplate.query(sql, new MedicamentoRowMapper());
    }

    /**
     * MODIFICADO: (Create) Salva uma prescrição E um lembrete no banco.
     */
    public void criarPrescricaoELembrete(Prescricao p, String horarioProgramado) throws SQLException {
        
        // 1. Inserir a Prescrição
        String sqlPrescricao = "INSERT INTO prescricao (id_medico, id_paciente, id_medicamento, data_inicio, dosagem, frequencia, instrucoes_adicionais) " +
                               "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlPrescricao, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, p.getId_medico());
            ps.setInt(2, p.getId_paciente());
            ps.setInt(3, p.getId_medicamento());
            ps.setDate(4, new Date(p.getData_inicio().getTime())); // java.sql.Date
            ps.setString(5, p.getDosagem());
            ps.setString(6, p.getFrequencia());
            ps.setString(7, p.getInstrucoes_adicionais());
            return ps;
        }, keyHolder);
        
        // 2. Obter o ID da prescrição que acabou de ser criada
        int idPrescricao = keyHolder.getKey().intValue();
        
        // 3. Inserir o Lembrete
        String sqlLembrete = "INSERT INTO lembrete (id_prescricao, horario_programado, status) VALUES (?, ?, 'Pendente')";
        
        // O MySQL aceita a string 'AAAA-MM-DD HH:mm:ss' diretamente para um campo DATETIME
        jdbcTemplate.update(sqlLembrete, idPrescricao, horarioProgramado);
    }
}