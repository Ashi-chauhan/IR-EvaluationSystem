package com.maynooth.project.IRSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import uk.ac.gla.terrier.jtreceval.trec_eval;

@SpringBootApplication
public class InformationRetrievalApplication {
	static String[][] output;
	static String resFile ="C:\\Users\\ashic\\OneDrive\\Desktop\\Project\\Result file_baselineindriBM25_EN_Run1.txt";
	static String qrel="C:\\Users\\ashic\\OneDrive\\Desktop\\Project\\qrel.CLEFeHealth2016Task3.txt";
	trec_eval t = new trec_eval();
	public static void main(String[] args) {
		System.out.println("main start");
		SpringApplication.run(InformationRetrievalApplication.class, args);
		//evaluate();
		System.out.println("main end");
	}

}
