package fr.miage.bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@SpringBootApplication
public class BankApplication {

	public static Map<String, String> countries = new HashMap<String, String>();

	public static void main(String[] args) {

		String[] isoCountries = Locale.getISOCountries();
		for (String iso : isoCountries) {
			Locale locale = new Locale("en_GB", iso);
			String countryName = locale.getDisplayCountry(locale);
			countries.put(countryName, iso);
		}

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
