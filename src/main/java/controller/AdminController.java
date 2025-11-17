package controller;

import dao.AdminDAO;
import model.Medico; // Importação Adicionada
import model.Paciente; // Importação Adicionada
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin") // URL base para endpoints de admin
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private AdminDAO adminDAO;

    // --- ENDPOINTS DE PACIENTE (CRUD Completo) ---

    /**
     * REQUISITO: (Read) Admin lê TODOS os pacientes da VIEW 'vw_medico_paciente'.
     */
    @GetMapping("/pacientes")
    public List<Map<String, Object>> getTodosPacientes() {
        return adminDAO.getTodosPacientes();
    }
    
    /**
     * REQUISITO: (Create) Admin chama a PROCEDURE 'sp_registrar_novo_paciente'.
     */
    @PostMapping("/pacientes")
    public ResponseEntity<Map<String, Object>> criarPaciente(@RequestBody Map<String, String> dados) {
        Map<String, Object> resposta = new HashMap<>();
        try {
            adminDAO.criarPaciente(dados);
            resposta.put("sucesso", true);
            resposta.put("mensagem", "Paciente criado com sucesso!");
            return ResponseEntity.ok(resposta);
            
        } catch (Exception e) {
            String mensagemErro = e.getMessage();
            // REQUISITO: Tratamento de erro do TRIGGER (CPF duplicado, data inválida, etc.)
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
     * NOVO: (Update) Atualiza um paciente
     */
    @PutMapping("/pacientes/{id}")
    public void atualizarPaciente(@PathVariable int id, @RequestBody Paciente paciente) {
        // O AdminDAO já possui este método
        adminDAO.atualizarPaciente(id, paciente);
    }

    /**
     * (Delete) Deleta um paciente
     */
    @DeleteMapping("/pacientes/{id}")
    public void deletarPaciente(@PathVariable int id) {
        adminDAO.deletarPaciente(id);
    }

    
    // --- ENDPOINTS DE MÉDICO (CRUD Completo) ---

    /**
     * (Read) Lista todos os médicos.
     */
    @GetMapping("/medicos")
    public List<Medico> getTodosMedicos() {
        return adminDAO.getTodosMedicos();
    }

    /**
     * (Create) Cria um médico E seu usuário
     * REQUISITO: Chama a FUNÇÃO 'proximo_id'
     */
    @PostMapping("/medicos")
    public ResponseEntity<Map<String, Object>> criarMedico(@RequestBody Map<String, String> dados) {
        Map<String, Object> resposta = new HashMap<>();
        try {
            Medico medico = new Medico();
            medico.setNome(dados.get("nome"));
            medico.setCrm(dados.get("crm"));
            medico.setEspecialidade(dados.get("especialidade"));
            String senha = dados.get("senha");

            adminDAO.criarMedico(medico, senha);
            resposta.put("sucesso", true);
            resposta.put("mensagem", "Médico criado com sucesso!");
            return ResponseEntity.ok(resposta);

        } catch (Exception e) {
            String mensagemErro = e.getMessage();
            if (mensagemErro.contains("Duplicate entry")) {
                resposta.put("mensagem", "Erro: O CRM informado já está cadastrado.");
            } else {
                resposta.put("mensagem", "Erro ao criar médico: " + mensagemErro);
            }
            resposta.put("sucesso", false);
            return ResponseEntity.badRequest().body(resposta);
        }
    }

    /**
     * NOVO: (Update) Atualiza um médico.
     */
    @PutMapping("/medicos/{id}")
    public void atualizarMedico(@PathVariable int id, @RequestBody Medico medico) {
        // O AdminDAO já possui este método
        adminDAO.atualizarMedico(id, medico);
    }

    /**
     * (Delete) Deleta um médico.
     */
    @DeleteMapping("/medicos/{id}")
    public void deletarMedico(@PathVariable int id) {
        adminDAO.deletarMedico(id);
    }
}