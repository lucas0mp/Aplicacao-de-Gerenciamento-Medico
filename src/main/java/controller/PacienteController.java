package controller;

import dao.DiarioMongoDAO;
import dao.PacienteDAO;
import model.DiarioPaciente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/paciente")
@CrossOrigin(origins = "*")
public class PacienteController {

    @Autowired
    private PacienteDAO pacienteDAO;

    @Autowired
    private DiarioMongoDAO diarioMongoDAO;

    @GetMapping("/{id}/lembretes")
    public List<Map<String, Object>> getLembretes(@PathVariable int id) {
        return pacienteDAO.getLembretesDoDia(id);
    }

    @PutMapping("/lembrete/{id}/tomar")
    public void marcarLembreteComoTomado(@PathVariable int id) {
        pacienteDAO.marcarLembreteComoTomado(id);
    }

    @GetMapping("/{id}/medicoes")
    public List<String> getMedicoes(@PathVariable int id) {
        return pacienteDAO.getHistoricoMedicoes(id);
    }

    // --- Endpoint de Prescrição REMOVIDO ---
    // O app.js não chama mais este endpoint

    // --- Endpoints do Diário (NoSQL) ---

    @PostMapping("/diario")
    public ResponseEntity<Map<String, Object>> salvarDiario(@RequestBody DiarioPaciente diario) {
        Map<String, Object> resposta = new HashMap<>();
        try {
            diarioMongoDAO.salvarDiario(diario); // Nome do método corrigido
            resposta.put("sucesso", true);
            resposta.put("mensagem", "Entrada do diário salva com sucesso!");
            return ResponseEntity.ok(resposta);
        } catch (Exception e) {
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Erro ao salvar no MongoDB: " + e.getMessage());
            return ResponseEntity.badRequest().body(resposta);
        }
    }
}