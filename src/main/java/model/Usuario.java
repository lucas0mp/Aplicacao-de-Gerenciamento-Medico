package model;
import java.util.Map;
// Classe para guardar o usuário logado
public class Usuario {
    private int idUsuario;
    private int idGrupo;
    private String nomeGrupo;
    private String nomeExibicao;
    private int idPerfil; 

    public Usuario(Map<String, Object> dadosUsuario) {
        this.idUsuario = (int) dadosUsuario.get("id_usuario");
        this.idGrupo = (int) dadosUsuario.get("id_grupo");
        this.nomeGrupo = (String) dadosUsuario.get("nome_grupo");
        if (this.idGrupo == 1) {
            this.nomeExibicao = "Administrador";
            this.idPerfil = (int) dadosUsuario.get("id_perfil_admin");
        } else if (this.idGrupo == 2) {
            this.nomeExibicao = (String) dadosUsuario.get("nome_medico");
            this.idPerfil = (int) dadosUsuario.get("id_perfil_medico");
        } else if (this.idGrupo == 3) {
            this.nomeExibicao = (String) dadosUsuario.get("nome_paciente");
            this.idPerfil = (int) dadosUsuario.get("id_perfil_paciente");
        }
    }
    // Getters
    public int getIdUsuario() {
        return idUsuario;
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public String getNomeGrupo() {
        return nomeGrupo;
    }

    public String getNomeExibicao() {
        return nomeExibicao;
    }

    public int getIdPerfil() {
        return idPerfil;
    }
}
