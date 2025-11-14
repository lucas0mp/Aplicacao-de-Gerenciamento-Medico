package model;

// REQUISITO POO: Herança
// Esta classe herda de Medicao, assim como a classe
// MedicaoGlicemia
// Este código é inspirado em
public class MedicaoPressao extends Medicao { 
    
    private double pressao_sistolica;
    private double pressao_diastolica;
    
    @Override
    // REQUISITO POO: Polimorfismo
    public String getTipoMedicao() { 
        return "Pressão Arterial"; 
    }
    
    // Getters e Setters
    public double getPressao_sistolica() {
        return pressao_sistolica;
    }
    public void setPressao_sistolica(double p) {
        this.pressao_sistolica = p;
    }
    public double getPressao_diastolica() {
        return pressao_diastolica;
    }
    public void setPressao_diastolica(double p) {
        this.pressao_diastolica = p;
    }
}