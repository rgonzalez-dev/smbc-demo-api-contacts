package rgonzalez.smbc.contacts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "rgonzalez.smbc.contacts.repository")
public class Microservice {

	public static void main(String[] args) {
		SpringApplication.run(Microservice.class, args);
	}

}
