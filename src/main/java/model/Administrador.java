package model;
// Inspirado em, adaptado
public class Administrador {
    private int id_admin;
    private String nome;
    private String email_contato;
    // Getters e Setters
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
}
