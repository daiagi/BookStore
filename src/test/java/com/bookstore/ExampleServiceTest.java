package com.bookstore;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ExampleServiceTest {

    @Test
    public void testGetGreeting() {
        ExampleService service = new ExampleService();
        assertEquals("Hello, World!", service.getGreeting());
    }
}
