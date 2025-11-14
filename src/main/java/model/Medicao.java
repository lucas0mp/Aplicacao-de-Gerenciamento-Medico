package model;
import java.util.Date;
public abstract class Medicao {
    protected int id_medicao;
    protected int id_paciente;
    protected Date data_hora;
    protected String observacoes;
    
    public abstract String getTipoMedicao(); // POO: Polimorfismo
    
    // Getters e Setters
    public int getId_medicao() {
        return id_medicao;
    }

    public void setId_medicao(int id) {
        this.id_medicao = id;
    }

    public int getId_paciente() {
        return id_paciente;
    }

    public void setId_paciente(int id) {
        this.id_paciente = id;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String o) {
        this.observacoes = o;
    }
}
