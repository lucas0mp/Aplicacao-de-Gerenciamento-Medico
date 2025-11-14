package config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import javax.sql.DataSource;

@Configuration
public class ConexaoDBConfig {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    // REQUISITO: Cria um "Pool de Conexão" para o usuário Admin
    @Bean(name = "adminDataSource")
    public DataSource adminDataSource(
            @Value("${spring.datasource.admin.username}") String user,
            @Value("${spring.datasource.admin.password}") String pass) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(user);
        dataSource.setPassword(pass);
        return dataSource;
    }

    // REQUISITO: Cria um "Pool de Conexão" para o usuário Médico
    @Bean(name = "medicoDataSource")
    public DataSource medicoDataSource(
            @Value("${spring.datasource.medico.username}") String user,
            @Value("${spring.datasource.medico.password}") String pass) {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setUrl(dbUrl);
        ds.setUsername(user);
        ds.setPassword(pass);
        return ds;
    }

    // REQUISITO: Cria um "Pool de Conexão" para o usuário Paciente
    @Bean(name = "pacienteDataSource")
    public DataSource pacienteDataSource(
            @Value("${spring.datasource.paciente.username}") String user,
            @Value("${spring.datasource.paciente.password}") String pass) {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setUrl(dbUrl);
        ds.setUsername(user);
        ds.setPassword(pass);
        return ds;
    }
}