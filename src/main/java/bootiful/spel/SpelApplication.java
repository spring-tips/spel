package bootiful.spel;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpelApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpelApplication.class, args);
	}

	@Bean
	ApplicationRunner illPutASpelOnYou() throws Exception {
		return args -> {


		};
	}
}
