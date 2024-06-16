package com.bookstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {

        // Load environment variables from .env file
        // Dotenv dotenv = Dotenv.load();
        // System.setProperty("DB_HOST", dotenv.get("DB_HOST"));
        // System.setProperty("DB_PORT", dotenv.get("DB_PORT"));
        // System.setProperty("DB_NAME", dotenv.get("DB_NAME"));
        // System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        // System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
        // System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
        // System.setProperty("JWT_EXPIRATION", dotenv.get("JWT_EXPIRATION"));


        
        SpringApplication.run(Application.class, args);
    }
}