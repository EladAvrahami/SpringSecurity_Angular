package com.supportportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.File;

import static com.supportportal.constant.FileConstant.USER_FOLDER;

@SpringBootApplication
public class SupportportalAplicationApplication {

	public static void main(String[] args) {

		SpringApplication.run(SupportportalAplicationApplication.class, args);
		System.out.println("starting");
		//any time the app is run it will generate this folders for me
		//the folders mentioned in the path that is in the constant USER_FOLDER
		new File(USER_FOLDER).mkdirs();
	}

	//35video 15:01
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder(){
		return new BCryptPasswordEncoder();
	}
}
