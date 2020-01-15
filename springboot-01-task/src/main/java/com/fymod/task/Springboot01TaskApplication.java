package com.fymod.task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Springboot01TaskApplication {

	public static void main(String[] args) {
		SpringApplication.run(Springboot01TaskApplication.class, args);
	}

}
