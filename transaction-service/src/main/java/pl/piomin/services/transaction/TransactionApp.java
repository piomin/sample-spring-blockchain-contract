package pl.piomin.services.transaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class TransactionApp {

    public static void main(String[] args) {
        SpringApplication.run(TransactionApp.class, args);
    }

    @Bean
    RestTemplate rest() {
    	return new RestTemplate();
    }
    
}
