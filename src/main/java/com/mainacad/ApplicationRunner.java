package com.mainacad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication()
public class ApplicationRunner
{
    public static void main( String[] args )
    {
        // Run with profiles
        SpringApplication context = new SpringApplication(ApplicationRunner.class);
        context.setAdditionalProfiles("rest");
        context.run(args);
    }
}