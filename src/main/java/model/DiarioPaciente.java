package model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import java.util.List;

// REQUISITO: @Document aponta para a coleção 'diario_paciente' no MongoDB
@Document(collection = "diario_paciente")
public class DiarioPaciente {
    @Id
    private String id; // ID do MongoDB
    private Integer idUsuarioSql; // Link para o 'usuarios.id_usuario'
    private Date data;
    private String titulo;
    private String textoLivre;
    private List<String> sintomas; // Schema flexível
    
    // Getters e Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getIdUsuarioSql() {
        return idUsuarioSql;
    }

    public void setIdUsuarioSql(Integer id) {
        this.idUsuarioSql = id;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date d) {
        this.data = d;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String t) {
        this.titulo = t;
    }

    public String getTextoLivre() {
        return textoLivre;
    }

    public void setTextoLivre(String t) {
        this.textoLivre = t;
    }

    public List<String> getSintomas() {
        return sintomas;
    }

    public void setSintomas(List<String> s) {
        this.sintomas = s;
    }
}
