package dao;

import model.Medico;
import model.Paciente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder; // Importe este
import org.springframework.jdbc.support.KeyHolder; // Importe este
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.PreparedStatement; // Importe este
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; // Importe este
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

@Repository
public class AdminDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AdminDAO(@Qualifier("adminDataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // --- CRUD DE PACIENTES (Funções do Admin) ---

    // (Este método de criar paciente estava correto)
    public void criarPaciente(Map<String, String> dados) throws Exception {
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

    public List<Map<String, Object>> getTodosPacientes() {
        String sql = "SELECT * FROM vw_medico_paciente"; // REQUISITO: Lendo da View
        return jdbcTemplate.queryForList(sql);
    }
    
    public void atualizarPaciente(int idPaciente, Paciente paciente) {
        String sql = "UPDATE paciente SET nome = ?, data_nascimento = ?, email = ?, telefone_celular = ? WHERE id_paciente = ?";
        jdbcTemplate.update(sql, paciente.getNome(), paciente.getData_nascimento(), paciente.getEmail(), paciente.getTelefone_celular(), idPaciente);
    }

    public void deletarPaciente(int idPaciente) {
        String sql = "DELETE FROM paciente WHERE id_paciente = ?";
        jdbcTemplate.update(sql, idPaciente);
    }


    // --- CRUD DE MÉDICOS (Funções do Admin) ---
    
    private static final class MedicoRowMapper implements RowMapper<Medico> {
        @Override
        public Medico mapRow(ResultSet rs, int rowNum) throws SQLException {
            Medico medico = new Medico();
            medico.setId_medico(rs.getInt("id_medico"));
            medico.setNome(rs.getString("nome"));
            medico.setCrm(rs.getString("crm"));
            medico.setEspecialidade(rs.getString("especialidade"));
            return medico;
        }
    }

    public List<Medico> getTodosMedicos() {
        String sql = "SELECT * FROM medico";
        return jdbcTemplate.query(sql, new MedicoRowMapper());
    }

    /**
     * (CRUD - Create) Cria um MÉDICO e um USUÁRIO para ele.
     * **** ESTE MÉTODO FOI CORRIGIDO ****
     * Inspirado em:
     */
    public void criarMedico(Medico medico, String senha) {
        // O Admin tem que fazer duas inserções:
        
        // 1. Criar o perfil do Medico (tabela 'medico')
        String sqlMedico = "INSERT INTO medico (crm, nome, especialidade) VALUES (?, ?, ?)";
        
        // REQUISITO: Usar GeneratedKeyHolder para pegar o ID de forma segura
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlMedico, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, medico.getCrm());
            ps.setString(2, medico.getNome());
            ps.setString(3, medico.getEspecialidade());
            return ps;
        }, keyHolder);
        
        // 2. Pegar o ID que acabou de ser criado
        int idMedico = keyHolder.getKey().intValue();
        
        // 3. Criar o Usuário de login (tabela 'usuarios')
        // REQUISITO: Chama a Função 'proximo_id'
        String sqlUsuario = "INSERT INTO usuarios (id_usuario, login, senha, id_grupo, id_perfil_medico) " +
                            "VALUES (proximo_id('seq_usuario'), ?, ?, 2, ?)";
        jdbcTemplate.update(sqlUsuario, medico.getCrm(), senha, idMedico); // Login do médico é o CRM
    }

    /**
     * (CRUD - Update) Atualiza um médico.
     * Inspirado em:
     */
    public void atualizarMedico(int idMedico, Medico medico) {
        String sql = "UPDATE medico SET nome = ?, crm = ?, especialidade = ? WHERE id_medico = ?";
        jdbcTemplate.update(sql, medico.getNome(), medico.getCrm(), medico.getEspecialidade(), idMedico);
        
        String sqlUser = "UPDATE usuarios SET login = ? WHERE id_perfil_medico = ?";
        jdbcTemplate.update(sqlUser, medico.getCrm(), idMedico);
    }

    /**
     * (CRUD - Delete) Deleta um médico.
     * Inspirado em:
     */
    public void deletarMedico(int idMedico) {
        String sql = "DELETE FROM medico WHERE id_medico = ?";
        jdbcTemplate.update(sql, idMedico);
    }
}