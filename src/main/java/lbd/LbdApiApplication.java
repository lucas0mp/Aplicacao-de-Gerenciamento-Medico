package lbd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

// @ComponentScan é CRÍTICO. Ele "avisa" ao Spring para
// procurar seus arquivos nos outros pacotes que criamos.
@SpringBootApplication
@ComponentScan(basePackages = {"config", "controller", "dao", "model"})
public class LbdApiApplication {

    public static void main(String[] args) {
        // Este é o novo "Main", ele inicia o servidor da API
        SpringApplication.run(LbdApiApplication.class, args);
        System.out.println("\nAPI INICIADA NA PORTA 8080!");
        System.out.println("Acesse http://localhost:8080 no seu navegador.");
    }
}