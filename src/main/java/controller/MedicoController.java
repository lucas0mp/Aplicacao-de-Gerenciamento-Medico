package controller;

import dao.MedicoDAO;
import model.Prescricao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
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
     * REQUISITO: (Create) Médico chama a PROCEDURE 'sp_registrar_novo_paciente'
     * (Inspirado em)
     */
    @PostMapping("/pacientes")
    public ResponseEntity<Map<String, Object>> criarPaciente(@RequestBody Map<String, String> dados) {
        Map<String, Object> resposta = new HashMap<>();
        try {
            // O ID do médico que está criando o paciente vem no JSON
            int idMedicoResponsavel = Integer.parseInt(dados.get("id_medico_responsavel"));
            
            // A API chama o DAO, que usa a conexão 'app_medico'
            medicoDAO.criarPaciente(dados, idMedicoResponsavel);
            resposta.put("sucesso", true);
            resposta.put("mensagem", "Paciente criado e vinculado ao médico com sucesso!");
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
     * REQUISITO: (Read) Médico lê seus pacientes da VIEW 'vw_medico_paciente'.
     */
    @GetMapping("/{idMedico}/pacientes")
    public List<Map<String, Object>> getMeusPacientes(@PathVariable int idMedico) {
        return medicoDAO.getMeusPacientes(idMedico);
    }

    /**
     * REQUISITO: (Create) Médico chama a PROCEDURE 'sp_registrar_medicao_glicemia'.
     */
    @PostMapping("/medicao/glicemia")
    public ResponseEntity<Map<String, Object>> registrarGlicemia(@RequestBody Map<String, Object> dados) {
        Map<String, Object> resposta = new HashMap<>();
        try {
            int idPaciente = (Integer) dados.get("idPaciente");
            double nivel = ((Number) dados.get("nivel")).doubleValue();
            String periodo = (String) dados.get("periodo");
            String obs = (String) dados.get("obs");
            
            medicoDAO.registrarGlicemia(idPaciente, nivel, periodo, obs);
            resposta.put("sucesso", true);
            resposta.put("mensagem", "Medição de glicemia registrada!");
            return ResponseEntity.ok(resposta);
        } catch (Exception e) {
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Erro ao registrar medição: " + e.getMessage());
            return ResponseEntity.badRequest().body(resposta);
        }
    }

    /**
     * REQUISITO: (Create) Médico registra medição de pressão
     * (Inspirado em)
     */
    @PostMapping("/medicao/pressao")
    public ResponseEntity<Map<String, Object>> registrarPressao(@RequestBody Map<String, Object> dados) {
        Map<String, Object> resposta = new HashMap<>();
        try {
            int idPaciente = (Integer) dados.get("idPaciente");
            double sistolica = ((Number) dados.get("sistolica")).doubleValue();
            double diastolica = ((Number) dados.get("diastolica")).doubleValue();
            String obs = (String) dados.get("obs");

            medicoDAO.registrarPressao(idPaciente, sistolica, diastolica, obs);
            resposta.put("sucesso", true);
            resposta.put("mensagem", "Medição de pressão registrada!");
            return ResponseEntity.ok(resposta);
        } catch (Exception e) {
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Erro ao registrar medição: " + e.getMessage());
            return ResponseEntity.badRequest().body(resposta);
        }
    }

    /**
     * REQUISITO: (Create) Médico cria prescrição e lembrete
     * (Inspirado em)
     */
    @PostMapping("/prescricao")
    public ResponseEntity<Map<String, Object>> criarPrescricao(@RequestBody Map<String, Object> dados) {
        Map<String, Object> resposta = new HashMap<>();
        try {
            // 1. Popula o objeto Prescricao
            Prescricao p = new Prescricao();
            p.setId_medico((Integer) dados.get("id_medico"));
            p.setId_paciente((Integer) dados.get("id_paciente"));
            p.setId_medicamento((Integer) dados.get("id_medicamento"));
            p.setDosagem((String) dados.get("dosagem"));
            p.setFrequencia((String) dados.get("frequencia"));
            
            SimpleDateFormat formatadorData = new SimpleDateFormat("yyyy-MM-dd");
            p.setData_inicio(formatadorData.parse((String) dados.get("data_inicio")));
            
            // 2. Popula o Lembrete
            SimpleDateFormat formatadorHora = new SimpleDateFormat("HH:mm");
            Date horarioLembrete = formatadorHora.parse((String) dados.get("horario_lembrete"));

            // 3. Chama o DAO
            medicoDAO.criarPrescricao(p, horarioLembrete);
            resposta.put("sucesso", true);
            resposta.put("mensagem", "Prescrição e lembrete criados!");
            return ResponseEntity.ok(resposta);
            
        } catch (Exception e) {
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Erro ao criar prescrição (verifique formatos AAAA-MM-DD e HH:mm): " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(resposta);
        }
    }
}