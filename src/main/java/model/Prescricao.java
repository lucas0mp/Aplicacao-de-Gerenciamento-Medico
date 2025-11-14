package model;
import java.util.Date;
public class Prescricao {
    private int id_prescricao;
    private int id_medico;
    private int id_paciente;
    private int id_medicamento;
    private Date data_inicio;
    private String dosagem;
    private String frequencia;
    // Getters e Setters
    public int getId_prescricao() {
        return id_prescricao;
    }

    public void setId_prescricao(int id) {
        this.id_prescricao = id;
    }

    public int getId_medico() {
        return id_medico;
    }

    public void setId_medico(int id) {
        this.id_medico = id;
    }

    public int getId_paciente() {
        return id_paciente;
    }

    public void setId_paciente(int id) {
        this.id_paciente = id;
    }

    public int getId_medicamento() {
        return id_medicamento;
    }

    public void setId_medicamento(int id) {
        this.id_medicamento = id;
    }

    public Date getData_inicio() {
        return data_inicio;
    }

    public void setData_inicio(Date d) {
        this.data_inicio = d;
    }

    public String getDosagem() {
        return dosagem;
    }

    public void setDosagem(String d) {
        this.dosagem = d;
    }

    public String getFrequencia() {
        return frequencia;
    }

    public void setFrequencia(String f) {
        this.frequencia = f;
    }
}
