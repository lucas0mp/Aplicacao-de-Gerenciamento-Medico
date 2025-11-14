package dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.util.Map;

@Repository
public class LoginDAO {

    private final JdbcTemplate jdbcTemplate;

    // REQUISITO: Pede a conexão de ADMIN para checar a tabela 'usuarios'
    @Autowired
    public LoginDAO(@Qualifier("adminDataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // REQUISITO: Valida contra 'usuarios' e 'grupos_usuarios'
    public Map<String, Object> validarLogin(String login, String senha) {
        String sql = "SELECT u.id_usuario, u.id_grupo, g.nome_grupo, " +
                     "u.id_perfil_paciente, u.id_perfil_medico, u.id_perfil_admin, " +
                     "p.nome as nome_paciente, m.nome as nome_medico " +
                     "FROM usuarios u " +
                     "JOIN grupos_usuarios g ON u.id_grupo = g.id_grupo " +
                     "LEFT JOIN paciente p ON u.id_perfil_paciente = p.id_paciente " +
                     "LEFT JOIN medico m ON u.id_perfil_medico = m.id_medico " +
                     "WHERE u.login = ? AND u.senha = ?";

        try {
            return jdbcTemplate.queryForMap(sql, login, senha);
        } catch (Exception e) {
            System.err.println("Falha no login: " + e.getMessage());
            return null; // Login falhou
        }
    }
}