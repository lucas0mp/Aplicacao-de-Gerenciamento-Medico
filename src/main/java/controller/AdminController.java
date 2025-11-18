package controller;

import dao.AdminDAO;
import model.Administrador;
import model.Medico;
import model.Paciente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/pacientes")
    public List<Map<String, Object>> getTodosPacientes() {
        return adminDAO.getTodosPacientes();
    }
    
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
        adminDAO.atualizarPaciente(id, paciente);
    }

    @DeleteMapping("/pacientes/{id}")
    public void deletarPaciente(@PathVariable int id) {
        adminDAO.deletarPaciente(id);
    }

    
    // --- ENDPOINTS DE MÉDICO (CRUD Completo) ---

    @GetMapping("/medicos")
    public List<Medico> getTodosMedicos() {
        return adminDAO.getTodosMedicos();
    }

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

    @PutMapping("/medicos/{id}")
    public void atualizarMedico(@PathVariable int id, @RequestBody Medico medico) {
        adminDAO.atualizarMedico(id, medico);
    }

    @DeleteMapping("/medicos/{id}")
    public void deletarMedico(@PathVariable int id) {
        adminDAO.deletarMedico(id);
    }

    // --- NOVO: ENDPOINTS DE ADMIN (CRUD - Sem Delete) ---

    /**
     * NOVO: (Read) Lista todos os administradores.
     * Chamado por: app.js -> carregarAdminsAdmin()
     */
    @GetMapping("/admins")
    public List<Administrador> getTodosAdmins() {
        return adminDAO.getTodosAdmins();
    }

    /**
     * NOVO: (Create) Cria um administrador.
     * Chamado por: app.js -> form-novo-admin
     */
    @PostMapping("/admins")
    public ResponseEntity<Map<String, Object>> criarAdmin(@RequestBody Map<String, String> dados) {
        Map<String, Object> resposta = new HashMap<>();
        try {
            Administrador admin = new Administrador();
            admin.setNome(dados.get("nome"));
            String login = dados.get("login"); // O email/login
            String senha = dados.get("senha");

            adminDAO.criarAdmin(admin, login, senha);
            resposta.put("sucesso", true);
            resposta.put("mensagem", "Administrador criado com sucesso!");
            return ResponseEntity.ok(resposta);

        } catch (Exception e) {
            String mensagemErro = e.getMessage();
            if (mensagemErro.contains("Duplicate entry")) {
                resposta.put("mensagem", "Erro: O Login (Email) informado já está cadastrado.");
            } else {
                resposta.put("mensagem", "Erro ao criar administrador: " + mensagemErro);
            }
            resposta.put("sucesso", false);
            return ResponseEntity.badRequest().body(resposta);
        }
    }

    /**
     * NOVO: (Update) Atualiza um administrador.
     * Chamado por: app.js -> btn-edit-admin
     */
    @PutMapping("/admins/{id}")
    public void atualizarAdmin(@PathVariable int id, @RequestBody Map<String, String> dados) {
        Administrador admin = new Administrador();
        admin.setNome(dados.get("nome"));
        String login = dados.get("login");
        
        adminDAO.atualizarAdmin(id, admin, login);
    }
}