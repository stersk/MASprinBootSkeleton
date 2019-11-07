package com.mainacad.configuration;

import lombok.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
@Profile("rest")
public class SwaggerConfig {
  private String buildVersion = "0.1";

  @Bean
  public Docket documentation() {
    return new Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.any())
            .paths(regex("/.*"))
            .build()
            .pathMapping("/")
            .apiInfo(metadata());
  }

  private ApiInfo metadata() {
    return new ApiInfoBuilder()
            .title("API documentation")
            .description("Use this documentation as a reference how to interact with app's API")
            .version(buildVersion)
            .contact(new Contact("Sters", "https://github.com/stersk/MAWebStore", "stersk@gmail.com"))
            .build();
  }
}
