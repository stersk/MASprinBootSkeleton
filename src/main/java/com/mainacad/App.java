package com.mainacad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication()
@EnableSwagger2
public class App
{
    public static void main( String[] args )
    {
        // SpringApplication.run(ApplicationRunner.class, args);

        // Run with profiles
        SpringApplication context = new SpringApplication(App.class);
        context.setAdditionalProfiles("dev");
        context.run(args);

    }
}