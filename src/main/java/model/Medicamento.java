package model;
public class Medicamento {
    private int id_medicamento;
    private String nome_comercial;
    private String principio_ativo;
    // Getters e Setters
    public int getId_medicamento() {
        return id_medicamento;
    }

    public void setId_medicamento(int id) {
        this.id_medicamento = id;
    }

    public String getNome_comercial() {
        return nome_comercial;
    }

    public void setNome_comercial(String n) {
        this.nome_comercial = n;
    }
}
