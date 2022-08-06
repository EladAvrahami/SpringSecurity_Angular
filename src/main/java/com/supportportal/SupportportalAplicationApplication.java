package com.supportportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class SupportportalAplicationApplication {

	public static void main(String[] args) {

		SpringApplication.run(SupportportalAplicationApplication.class, args);
		System.out.println("starting");
	}

	//35video 15:01
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder(){
		return new BCryptPasswordEncoder();
	}
}
