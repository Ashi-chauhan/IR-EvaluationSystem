package com.maynooth.project.IRSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import uk.ac.gla.terrier.jtreceval.trec_eval;

@SpringBootApplication
public class InformationRetrievalApplication {
	public static void main(String[] args) {
		System.out.println("main start");
		SpringApplication.run(InformationRetrievalApplication.class, args);
		//evaluate();
		System.out.println("main end");
	}

}
