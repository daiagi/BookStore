// package com.bookstore.model;

// import org.springframework.data.annotation.Id;
// import org.springframework.data.mongodb.core.mapping.Document;

// import java.util.List;

// @Document(collection = "shoppingCarts")
// public class ShoppingCart {
//     @Id
//     private String id;
//     private String userId;
//     private List<CartItem> items;

//     // Getters and setters

//     public String getId() {
//         return id;
//     }

//     public void setId(String id) {
//         this.id = id;
//     }

//     public String getUserId() {
//         return userId;
//     }

//     public void setUserId(String userId) {
//         this.userId = userId;
//     }

//     public List<CartItem> getItems() {
//         return items;
//     }

//     public void setItems(List<CartItem> items) {
//         this.items = items;
//     }

//     public static class CartItem {
//         private String bookId;
//         private int quantity;

//         // Getters and setters
//         public String getBookId() {
//             return bookId;
//         }

//         public void setBookId(String bookId) {
//             this.bookId = bookId;
//         }

//         public int getQuantity() {
//             return quantity;
//         }

//         public void setQuantity(int quantity) {
//             this.quantity = quantity;
//         }
//     }
// }
