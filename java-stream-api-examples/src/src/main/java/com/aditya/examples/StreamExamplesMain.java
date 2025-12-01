package com.aditya.examples;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StreamExamplesMain implements CommandLineRunner {
    
    public static void main(String[] args) {
        SpringApplication.run(StreamExamplesMain.class, args);
    }
    
    @Override
    public void run(String... args) {
        //StreamCreationExamples.demonstrateAll();
        //StreamPractice.getEvenNumbers();
        //BasicStreamExmples.getFirstFiveElements();
        //IntermidiateStreamExamples.filterPositiveNumbers();
        //AdvancedStreamExamples.flattenLists();
        ComplexExamples.getStringOutOfNumbers();
    }
}