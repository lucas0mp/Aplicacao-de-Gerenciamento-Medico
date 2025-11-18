package model;

import java.util.Date; // Importa java.util.Date

/**
 * Modelo para a tabela 'prescricao'.
 * Inspirado em: projetopoo-final/src/model/Prescricao.java
 */
public class Prescricao {
    
    private int id_prescricao;
    private int id_medico;
    private int id_paciente;
    private int id_medicamento;
    private Date data_inicio; // Importante: java.util.Date
    private String dosagem;
    private String frequencia;
    private String instrucoes_adicionais;

    // Getters e Setters
    
    public int getId_prescricao() {
        return id_prescricao;
    }

    public void setId_prescricao(int id_prescricao) {
        this.id_prescricao = id_prescricao;
    }

    public int getId_medico() {
        return id_medico;
    }

    public void setId_medico(int id_medico) {
        this.id_medico = id_medico;
    }

    public int getId_paciente() {
        return id_paciente;
    }

    public void setId_paciente(int id_paciente) {
        this.id_paciente = id_paciente;
    }

    public int getId_medicamento() {
        return id_medicamento;
    }

    public void setId_medicamento(int id_medicamento) {
        this.id_medicamento = id_medicamento;
    }

    public Date getData_inicio() {
        return data_inicio;
    }

    public void setData_inicio(Date data_inicio) {
        this.data_inicio = data_inicio;
    }

    public String getDosagem() {
        return dosagem;
    }

    public void setDosagem(String dosagem) {
        this.dosagem = dosagem;
    }

    public String getFrequencia() {
        return frequencia;
    }

    public void setFrequencia(String frequencia) {
        this.frequencia = frequencia;
    }

    public String getInstrucoes_adicionais() {
        return instrucoes_adicionais;
    }

    public void setInstrucoes_adicionais(String instrucoes_adicionais) {
        this.instrucoes_adicionais = instrucoes_adicionais;
    }
}