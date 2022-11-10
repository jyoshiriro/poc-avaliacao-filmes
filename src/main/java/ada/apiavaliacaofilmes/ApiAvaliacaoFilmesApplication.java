package ada.apiavaliacaofilmes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
//@EnableGlobalMethodSecurity(securedEnabled = true)
@EnableFeignClients
public class ApiAvaliacaoFilmesApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiAvaliacaoFilmesApplication.class, args);
	}

}
