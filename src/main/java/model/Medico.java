package model;

// Inspirado em, mas adaptado
public class Medico {
    private int id_medico;
    private String crm;
    private String nome;
    private String especialidade; // Este campo faltava nos getters/setters

    // --- Getters e Setters COMPLETOS ---
    
    public int getId_medico() {
        return id_medico;
    }
    public void setId_medico(int id_medico) {
        this.id_medico = id_medico;
    }
    public String getCrm() {
        return crm;
    }
    public void setCrm(String crm) {
        this.crm = crm;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    // --- MÉTODOS QUE FALTAVAM ---
    public String getEspecialidade() {
        return especialidade;
    }
    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }
}