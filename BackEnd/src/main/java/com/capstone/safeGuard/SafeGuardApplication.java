package com.capstone.safeGuard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:application-secrets.properties")
@SpringBootApplication
public class SafeGuardApplication {

	public static void main(String[] args) {
		SpringApplication.run(SafeGuardApplication.class, args);
	}

}
