package controller;

import dao.MedicoDAO;
import model.Paciente; // Importação Adicionada
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/medico") // URL base para endpoints de médico
@CrossOrigin(origins = "*")
public class MedicoController {

    @Autowired
    private MedicoDAO medicoDAO;

    /**
     * REQUISITO: (Read) Endpoint para o médico logado ver SEUS pacientes.
     */
    @GetMapping("/{id}/pacientes")
    public List<Map<String, Object>> getMeusPacientes(@PathVariable int id) {
        return medicoDAO.getPacientesPorMedico(id);
    }

    /**
     * REQUISITO: (Create) Endpoint para o médico registrar um NOVO paciente.
     */
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
    
    /**
     * NOVO: (Update) Médico atualiza um paciente.
     */
    @PutMapping("/pacientes/{id}")
    public void atualizarPaciente(@PathVariable int id, @RequestBody Paciente paciente) {
        medicoDAO.atualizarPaciente(id, paciente);
    }

    /**
     * NOVO: (Delete) Médico deleta um paciente.
     */
    @DeleteMapping("/pacientes/{id}")
    public void deletarPaciente(@PathVariable int id) {
        medicoDAO.deletarPaciente(id);
    }


    /**
     * REQUISITO: (Create) Endpoint para o médico registrar uma medição de glicemia.
     */
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
}