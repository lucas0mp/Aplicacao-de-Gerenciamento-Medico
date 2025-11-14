package model;

public class DoencaCronica {

    private int id_doenca;
    private String nome_cientifico;
    private String nome_popular;

    // Getters e Setters
    public int getId_doenca() {
        return id_doenca;
    }

    public void setId_doenca(int id_doenca) {
        this.id_doenca = id_doenca;
    }

    public String getNome_cientifico() {
        return nome_cientifico;
    }

    public void setNome_cientifico(String nome_cientifico) {
        this.nome_cientifico = nome_cientifico;
    }

    public String getNome_popular() {
        return nome_popular;
    }

    public void setNome_popular(String nome_popular) {
        this.nome_popular = nome_popular;
    }
}