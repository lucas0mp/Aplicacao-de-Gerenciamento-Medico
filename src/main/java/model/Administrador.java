package model;

// Inspirado em, adaptado
public class Administrador {
    private int id_admin;
    private String nome;
    private String email_contato;
    
    // --- CAMPO ADICIONADO ---
    // Este campo não existe na tabela 'administrador', 
    // mas vamos usá-lo para guardar o login que vem da tabela 'usuarios'.
    private String login;

    // --- Getters e Setters ---
    
    public int getId_admin() {
        return id_admin;
    }

    public void setId_admin(int id) {
        this.id_admin = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String n) {
        this.nome = n;
    }

    public String getEmail_contato() {
        return email_contato;
    }

    public void setEmail_contato(String e) {
        this.email_contato = e;
    }
    
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}