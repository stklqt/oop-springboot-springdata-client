package de.andrena.springworkshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
//for jsr310 java 8 java.time.*
@EntityScan(basePackageClasses = {SpringworkshopApplication.class})
public class SpringworkshopApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringworkshopApplication.class, args);
	}
}
