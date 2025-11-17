package dao;

import model.Paciente; // Importação Adicionada
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

@Repository
public class MedicoDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MedicoDAO(@Qualifier("medicoDataSource") DataSource dataSource) {
        // REQUISITO: Usa a conexão do MÉDICO (app_medico), com permissões limitadas
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * REQUISITO: (Read) Médico lê APENAS SEUS pacientes da VIEW 'vw_medico_paciente'.
     */
    public List<Map<String, Object>> getPacientesPorMedico(int idMedico) {
        String sql = "SELECT * FROM vw_medico_paciente WHERE id_medico = ?";
        return jdbcTemplate.queryForList(sql, idMedico);
    }

    /**
     * REQUISITO: (Create) Médico chama a PROCEDURE 'sp_registrar_novo_paciente'.
     */
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

    /**
     * REQUISITO: (Create) Médico chama a PROCEDURE 'sp_registrar_medicao_glicemia'.
     */
    public void registrarGlicemia(Map<String, String> dados) throws SQLException {
        String sql = "{CALL sp_registrar_medicao_glicemia(?, ?, ?, ?)}";
        
        jdbcTemplate.execute(sql, (CallableStatement cs) -> {
            cs.setInt(1, Integer.parseInt(dados.get("idPaciente")));
            cs.setDouble(2, Double.parseDouble(dados.get("nivel")));
            cs.setString(3, dados.get("periodo"));
            cs.setString(4, dados.get("obs")); // "Registrado pelo(a) " + sessao.nome
            cs.execute();
            return null;
        });
    }
    
    /**
     * NOVO: (Update) Médico atualiza dados de um paciente.
     */
    public void atualizarPaciente(int idPaciente, Paciente paciente) {
        // O app_medico tem permissão de UPDATE na tabela 'paciente'
        String sql = "UPDATE paciente SET nome = ?, data_nascimento = ?, email = ?, telefone_celular = ? WHERE id_paciente = ?";
        jdbcTemplate.update(sql, paciente.getNome(), paciente.getData_nascimento(), paciente.getEmail(), paciente.getTelefone_celular(), idPaciente);
    }

    /**
     * NOVO: (Delete) Médico deleta um paciente.
     */
    public void deletarPaciente(int idPaciente) {
        // O app_medico precisa da permissão de DELETE (ver esquemabancodedados.txt)
        String sql = "DELETE FROM paciente WHERE id_paciente = ?";
        jdbcTemplate.update(sql, idPaciente);
    }
}