package model;
public class MedicaoGlicemia extends Medicao { // Herda de Medicao
    private double nivel_glicose;
    private String periodo;
    
    @Override
    public String getTipoMedicao() { return "Glicemia"; } // Polimorfismo
    
    // Getters e Setters
    public double getNivel_glicose() {
        return nivel_glicose;
    }

    public void setNivel_glicose(double n) {
        this.nivel_glicose = n;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String p) {
        this.periodo = p;
    }
}
