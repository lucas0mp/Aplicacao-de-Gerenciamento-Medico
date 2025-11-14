package model;
import java.util.Date;
public class Lembrete {
    private int id_lembrete;
    private int id_prescricao;
    private Date horario_programado;
    private String status;
    // Getters e Setters
    public int getId_lembrete() {
        return id_lembrete;
    }

    public void setId_lembrete(int id) {
        this.id_lembrete = id;
    }

    public int getId_prescricao() {
        return id_prescricao;
    }

    public void setId_prescricao(int id) {
        this.id_prescricao = id;
    }

    public Date getHorario_programado() {
        return horario_programado;
    }

    public void setHorario_programado(Date h) {
        this.horario_programado = h;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String s) {
        this.status = s;
    }
}
