package controller;

import dao.DiarioMongoDAO;
import dao.PacienteDAO;
import model.DiarioPaciente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/paciente") // URL base para endpoints de paciente
@CrossOrigin(origins = "*")
public class PacienteController {

    @Autowired
    private PacienteDAO pacienteDAO;
    
    @Autowired
    private DiarioMongoDAO diarioMongoDAO;

    /**
     * REQUISITO: (Read) Paciente acessa a VIEW de lembretes.
     * (Inspirado em)
     */
    @GetMapping("/{idPaciente}/lembretes")
    public List<Map<String, Object>> getLembretes(@PathVariable int idPaciente) {
        return pacienteDAO.getLembretes(idPaciente);
    }
    
    /**
     * REQUISITO: (Read) Paciente acessa seu histórico de medições.
     * (Inspirado em)
     */
    @GetMapping("/{idPaciente}/medicoes")
    public List<String> getMedicoes(@PathVariable int idPaciente) {
        return pacienteDAO.getMedicoes(idPaciente);
    }

    /**
     * REQUISITO: (Update) Paciente atualiza lembrete para 'Tomado'.
     */
    @PutMapping("/lembrete/{idLembrete}/tomar")
    public ResponseEntity<Void> marcarComoTomado(@PathVariable int idLembrete) {
        try {
            pacienteDAO.marcarLembreteComoTomado(idLembrete);
            return ResponseEntity.ok().build(); // Retorna 200 OK
        } catch (Exception e) {
            return ResponseEntity.badRequest().build(); // Retorna 400 Bad Request
        }
    }

    /**
     * REQUISITO: (Create) Paciente salva no Banco NoSQL (MongoDB).
     */
    @PostMapping("/diario")
    public ResponseEntity<Map<String, Object>> salvarDiario(@RequestBody DiarioPaciente diario) {
        Map<String, Object> resposta = new HashMap<>();
        try {
            diario.setData(new Date()); // Define a data atual no servidor
            diarioMongoDAO.salvarDiario(diario);
            resposta.put("sucesso", true);
            resposta.put("mensagem", "Diário salvo no MongoDB!");
            return ResponseEntity.ok(resposta);
        } catch (Exception e) {
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Erro ao salvar no MongoDB: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(resposta);
        }
    }
}