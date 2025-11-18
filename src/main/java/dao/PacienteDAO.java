package dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class PacienteDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PacienteDAO(@Qualifier("pacienteDataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * REQUISITO: (Read) Paciente lê dados da VIEW 'vw_lembretes_hoje'.
     */
    public List<Map<String, Object>> getLembretesDoDia(int idPaciente) {
        
        // --- CORREÇÃO AQUI ---
        // O nome da View correta é 'vw_lembretes_hoje'.
        // Eu tinha escrito 'vw_lembretes_hoje_com_id' por engano na versão anterior.
        String sql = "SELECT * FROM vw_lembretes_hoje WHERE id_paciente = ?";
        // ---------------------
        
        return jdbcTemplate.queryForList(sql, idPaciente);
    }

    /**
     * REQUISITO: (Update) Paciente atualiza o status de um lembrete.
     */
    public void marcarLembreteComoTomado(int idLembrete) {
        // A View vw_lembretes_hoje (que corrigimos) agora fornece o id_lembrete
        String sql = "UPDATE lembrete SET status = 'Tomado' WHERE id_lembrete = ?";
        jdbcTemplate.update(sql, idLembrete);
    }

    /**
     * REQUISITO: (Read) Paciente lê seu histórico de medições.
     */
    public List<String> getHistoricoMedicoes(int idPaciente) {
        List<String> historico = new ArrayList<>();
        
        // 1. Busca Glicemia
        String sqlGlic = "SELECT m.data_hora, mg.nivel_glicose, mg.periodo " +
                         "FROM medicao m JOIN medicao_glicemia mg ON m.id_medicao = mg.id_medicao " +
                         "WHERE m.id_paciente = ? ORDER BY m.data_hora DESC";
        
        jdbcTemplate.query(sqlGlic, (ResultSet rs) -> {
            String data = rs.getTimestamp("data_hora").toString();
            String nivel = rs.getString("nivel_glicose");
            String periodo = rs.getString("periodo");
            historico.add(String.format("[%s] GLICEMIA: %s mg/dL (%s)", data, nivel, periodo));
        }, idPaciente);

        // 2. Busca Pressão
        String sqlPres = "SELECT m.data_hora, mp.pressao_sistolica, mp.pressao_diastolica " +
                         "FROM medicao m JOIN medicao_pressao mp ON m.id_medicao = mp.id_medicao " +
                         "WHERE m.id_paciente = ? ORDER BY m.data_hora DESC";
        
        jdbcTemplate.query(sqlPres, (ResultSet rs) -> {
            String data = rs.getTimestamp("data_hora").toString();
            String sis = rs.getString("pressao_sistolica");
            String dia = rs.getString("pressao_diastolica");
            historico.add(String.format("[%s] PRESSÃO: %s / %s mmHg", data, sis, dia));
        }, idPaciente);

        return historico;
    }
}