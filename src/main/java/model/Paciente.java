package model;
import java.util.Date;
// Inspirado em, mas adaptado
public class Paciente {
    private int id_paciente;
    private String nome;
    private String cpf;
    private Date data_nascimento;
    private String telefone_celular;
    private String email;

    // --- Getters e Setters COMPLETOS ---
    public int getId_paciente() {
        return id_paciente;
    }

    public void setId_paciente(int id) {
        this.id_paciente = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String n) {
        this.nome = n;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String c) {
        this.cpf = c;
    }

    public Date getData_nascimento() {
        return data_nascimento;
    }

    public void setData_nascimento(Date d) {
        this.data_nascimento = d;
    }

    public String getTelefone_celular() {
        return telefone_celular;
    }

    public void setTelefone_celular(String t) {
        this.telefone_celular = t;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String e) {
        this.email = e;
    }
}
