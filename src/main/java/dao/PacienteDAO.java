package dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
public class PacienteDAO {
    
    // REQUISITO: Usa a conexão de Paciente (mínima permissão)
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PacienteDAO(@Qualifier("pacienteDataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * REQUISITO: Lê da VIEW 'vw_lembretes_hoje'
     * (Inspirado em `listarPorPaciente` do POO)
     */
    public List<Map<String, Object>> getLembretes(int idPaciente) {
        // A VIEW já filtra por status='Pendente' e data=CURDATE()
        String sql = "SELECT * FROM vw_lembretes_hoje WHERE id_paciente = ?";
        return jdbcTemplate.queryForList(sql, idPaciente);
    }
    
    /**
     * REQUISITO: Lê da FUNCTION 'fn_contar_prescricoes_ativas'
     */
    public int contarPrescricoesAtivas(int idPaciente) {
        String sql = "SELECT fn_contar_prescricoes_ativas(?)";
        Integer total = jdbcTemplate.queryForObject(sql, Integer.class, idPaciente);
        return (total != null) ? total : 0;
    }

    /**
     * REQUISITO: Paciente pode atualizar o status de um lembrete (marcar como 'Tomado')
     * A conexão 'app_paciente' tem permissão de UPDATE na tabela 'lembrete'.
     */
    public void marcarLembreteComoTomado(int idLembrete) {
        String sql = "UPDATE lembrete SET status = 'Tomado' WHERE id_lembrete = ?";
        jdbcTemplate.update(sql, idLembrete);
    }

    /**
     * Busca o histórico de medições (Glicemia e Pressão).
     * (Inspirado em `listarMedicoesPorPaciente` do POO)
     */
    public List<String> getMedicoes(int idPaciente) {
        // REQUISITO: 'app_paciente' tem SELECT em 'medicao', 'medicao_glicemia', 'medicao_pressao'
        String sql = "SELECT m.data_hora, m.observacoes, " +
                     "mg.nivel_glicose, mg.periodo, " +
                     "mp.pressao_sistolica, mp.pressao_diastolica " +
                     "FROM medicao m " +
                     "LEFT JOIN medicao_glicemia mg ON m.id_medicao = mg.id_medicao " +
                     "LEFT JOIN medicao_pressao mp ON m.id_medicao = mp.id_medicao " +
                     "WHERE m.id_paciente = ? " +
                     "ORDER BY m.data_hora DESC LIMIT 10"; // Limita aos 10 mais recentes

        // RowMapper para formatar a string do jeito que o POO fazia
        RowMapper<String> mapper = (ResultSet rs, int rowNum) -> {
            String tipo = "Tipo não identificado";
            String valor = "";
            
            double glicose = rs.getDouble("nivel_glicose");
            if (!rs.wasNull()) { 
                tipo = "Glicemia";
                valor = glicose + " mg/dL (Período: " + rs.getString("periodo") + ")";
            } else {
                double sistolica = rs.getDouble("pressao_sistolica");
                if (!rs.wasNull()) { 
                    tipo = "Pressão Arterial";
                    valor = sistolica + " x " + rs.getDouble("pressao_diastolica");
                }
            }
            
            return String.format("Data: %s | Tipo: %s | Valor: %s | Obs: %s",
                    rs.getTimestamp("data_hora").toString(),
                    tipo,
                    valor,
                    rs.getString("observacoes"));
        };
        
        return jdbcTemplate.query(sql, mapper, idPaciente);
    }
}