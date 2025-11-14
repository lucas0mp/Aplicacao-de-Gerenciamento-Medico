package controller;

import dao.LoginDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") 
public class LoginController {

    @Autowired
    private LoginDAO loginDAO; 
    
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> credenciais) {
        Map<String, Object> resposta = new HashMap<>();
        String login = credenciais.get("login");
        String senha = credenciais.get("senha");
        
        Map<String, Object> dadosUsuario = loginDAO.validarLogin(login, senha);

        if (dadosUsuario != null) {
            resposta.put("sucesso", true);
            resposta.put("dadosUsuario", dadosUsuario);
            
            int idGrupo = (int) dadosUsuario.get("id_grupo");
            if (idGrupo == 2) {
                resposta.put("nome", dadosUsuario.get("nome_medico"));
            } else if (idGrupo == 3) {
                resposta.put("nome", dadosUsuario.get("nome_paciente"));
            } else {
                resposta.put("nome", "Admin");
            }
        } else {
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Login ou senha inválidos.");
        }
        return resposta; 
    }
}