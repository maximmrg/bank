package fr.miage.bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class BankApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankApplication.class, args);
	}

	@Bean
	PasswordEncoder passwordEncoder(){
		return new Argon2PasswordEncoder();
	}

	@Bean
	Argon2PasswordEncoder argon2PasswordEncoder(){
		return new Argon2PasswordEncoder();
	}

}
