package com.bookstore;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/book-store/health")
    public String healthCheck() {
        return "book-store is up and running";
    }
}
