package com.tunely;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TunelyApplication {

	public static void main(String[] args) {
		SpringApplication.run(TunelyApplication.class, args);
	}

}
