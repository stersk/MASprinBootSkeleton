package com.mainacad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication()
public class ApplicationRunner
{
    public static void main(String[] args)
    {
        SpringApplication context = new SpringApplication(ApplicationRunner.class);
        context.setAdditionalProfiles("web");
        context.run(args);
    }
}