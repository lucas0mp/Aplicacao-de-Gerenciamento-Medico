package model;

/**
 * Modelo para a tabela 'medicamento'.
 * Inspirado em: projetopoo-final/src/model/Medicamento.java
 */
public class Medicamento {
    
    private int id_medicamento;
    private String nome_comercial;
    private String principio_ativo;

    // Getters e Setters
    
    public int getId_medicamento() {
        return id_medicamento;
    }

    public void setId_medicamento(int id_medicamento) {
        this.id_medicamento = id_medicamento;
    }

    public String getNome_comercial() {
        return nome_comercial;
    }

    public void setNome_comercial(String nome_comercial) {
        this.nome_comercial = nome_comercial;
    }

    public String getPrincipio_ativo() {
        return principio_ativo;
    }

    public void setPrincipio_ativo(String principio_ativo) {
        this.principio_ativo = principio_ativo;
    }
}