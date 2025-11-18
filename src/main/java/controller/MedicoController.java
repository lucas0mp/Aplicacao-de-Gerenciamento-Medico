package controller;

import dao.MedicoDAO;
import model.Medicamento; 
import model.Paciente;
import model.Prescricao; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat; 
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/medico") 
@CrossOrigin(origins = "*")
public class MedicoController {

    @Autowired
    private MedicoDAO medicoDAO;

    // --- Endpoints de Pacientes (CRUD) ---

    @GetMapping("/{id}/pacientes")
    public List<Map<String, Object>> getMeusPacientes(@PathVariable int id) {
        return medicoDAO.getPacientesPorMedico(id);
    }

    @PostMapping("/pacientes")
    public ResponseEntity<Map<String, Object>> criarPaciente(@RequestBody Map<String, String> dados) {
        Map<String, Object> resposta = new HashMap<>();
        try {
            medicoDAO.criarPacienteMedico(dados);
            resposta.put("sucesso", true);
            resposta.put("mensagem", "Paciente criado com sucesso!");
            return ResponseEntity.ok(resposta);

        } catch (Exception e) {
            String mensagemErro = e.getMessage();
            if (mensagemErro.contains("Duplicate entry")) {
                resposta.put("mensagem", "Erro: O CPF ou Email informado já está cadastrado.");
            } else if (mensagemErro.contains("Formato de data inválido")) {
                resposta.put("mensagem", "Erro: Formato de data inválido. Use AAAA-MM-DD.");
            } else {
                resposta.put("mensagem", "Erro ao criar paciente: " + mensagemErro);
            }
            resposta.put("sucesso", false);
            return ResponseEntity.badRequest().body(resposta);
        }
    }
    
    @PutMapping("/pacientes/{id}")
    public void atualizarPaciente(@PathVariable int id, @RequestBody Paciente paciente) {
        medicoDAO.atualizarPaciente(id, paciente);
    }

    @DeleteMapping("/pacientes/{id}")
    public void deletarPaciente(@PathVariable int id) {
        medicoDAO.deletarPaciente(id);
    }

    // --- Endpoint de Medição ---

    @PostMapping("/medicao/glicemia")
    public ResponseEntity<Map<String, Object>> registrarGlicemia(@RequestBody Map<String, String> dados) {
        Map<String, Object> resposta = new HashMap<>();
        try {
            medicoDAO.registrarGlicemia(dados);
            resposta.put("sucesso", true);
            resposta.put("mensagem", "Medição de glicemia registrada com sucesso!");
            return ResponseEntity.ok(resposta);
            
        } catch (Exception e) {
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Erro ao registrar medição: " + e.getMessage());
            return ResponseEntity.badRequest().body(resposta);
        }
    }

    // --- Endpoints de Prescrição (MODIFICADO) ---

    @GetMapping("/medicamentos")
    public List<Medicamento> getMedicamentos() {
        return medicoDAO.getTodosMedicamentos();
    }

    /**
     * MODIFICADO: (Create) Cria uma nova prescrição E o seu lembrete.
     */
    @PostMapping("/prescricoes")
    public ResponseEntity<Map<String, Object>> criarPrescricao(@RequestBody Map<String, String> dados) {
        Map<String, Object> resposta = new HashMap<>();
        try {
            Prescricao p = new Prescricao();
            p.setId_medico(Integer.parseInt(dados.get("id_medico")));
            p.setId_paciente(Integer.parseInt(dados.get("id_paciente")));
            p.setId_medicamento(Integer.parseInt(dados.get("id_medicamento")));
            p.setDosagem(dados.get("dosagem"));
            p.setFrequencia(dados.get("frequencia"));
            p.setInstrucoes_adicionais(dados.get("instrucoes_adicionais"));

            // Converte a data de início
            SimpleDateFormat formatadorData = new SimpleDateFormat("yyyy-MM-dd");
            p.setData_inicio(formatadorData.parse(dados.get("data_inicio")));
            
            // NOVO: Pega a string DATETIME completa do lembrete
            String horarioProgramado = dados.get("horario_programado"); // Ex: "2025-11-18 08:00:00"

            // Chama o DAO (método modificado)
            medicoDAO.criarPrescricaoELembrete(p, horarioProgramado); 
            
            resposta.put("sucesso", true);
            resposta.put("mensagem", "Prescrição e Lembrete criados com sucesso!");
            return ResponseEntity.ok(resposta);
            
        } catch (Exception e) {
            String msgErro = e.getMessage();
            if (msgErro.contains("data de início retroativa")) {
                 resposta.put("mensagem", "Erro: Não é permitido criar prescrições com data de início no passado.");
            } else {
                 resposta.put("mensagem", "Erro ao criar prescrição: " + msgErro);
            }
            resposta.put("sucesso", false);
            return ResponseEntity.badRequest().body(resposta);
        }
    }
}