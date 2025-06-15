package com.example.Bank_Star.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI bankStarOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bank_Star API")
                        .description("API для системы рекомендаций Bank_Star")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Bank_Star")
                                .url("https://example.com")
                                .email("email@example.com"))
                        .license(new License()
                                .name("License of API")
                                .url("API license URL")));
    }
}