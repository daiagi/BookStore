package com.bookstore.dto;

public class BookDTO {
    private String title;
    private String author;
    private String description;
    private double price;
    private String category;

    // Constructors
    public BookDTO() {}

    public BookDTO(String title, String author, String description, double price, String category) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.price = price;
        this.category = category;
    }

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}
