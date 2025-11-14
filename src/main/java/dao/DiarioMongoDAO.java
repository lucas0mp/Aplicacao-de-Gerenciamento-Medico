package dao;

import model.DiarioPaciente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DiarioMongoDAO {

    // O Spring Boot injeta o MongoTemplate automaticamente
    // usando a string 'spring.data.mongodb.uri' do application.properties
    @Autowired
    private MongoTemplate mongoTemplate;

    // REQUISITO: Salva o documento flexível no MongoDB
    public DiarioPaciente salvarDiario(DiarioPaciente diario) {
        // O .save insere o documento na coleção 'diario_paciente'
        // (definida na classe model.DiarioPaciente)
        return mongoTemplate.save(diario);
    }
}