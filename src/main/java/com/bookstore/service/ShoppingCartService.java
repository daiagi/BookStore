// package com.bookstore.service;

// import com.bookstore.model.ShoppingCart;
// import com.bookstore.repository.ShoppingCartRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import java.util.Optional;

// @Service
// public class ShoppingCartService {
//     private final ShoppingCartRepository shoppingCartRepository;

//     @Autowired
//     public ShoppingCartService(ShoppingCartRepository shoppingCartRepository) {
//         this.shoppingCartRepository = shoppingCartRepository;
//     }

//     public ShoppingCart getCartByUserId(String userId) {
//         return shoppingCartRepository.findByUserId(userId).orElse(new ShoppingCart());
//     }

//     public ShoppingCart addToCart(String userId, ShoppingCart.CartItem item) {
//         ShoppingCart cart = getCartByUserId(userId);
//         cart.setUserId(userId);
//         cart.getItems().add(item);
//         return shoppingCartRepository.save(cart);
//     }

//     public ShoppingCart removeFromCart(String userId, String bookId) {
//         ShoppingCart cart = getCartByUserId(userId);
//         cart.setItems(cart.getItems().stream()
//                 .filter(item -> !item.getBookId().equals(bookId))
//                 .toList());
//         return shoppingCartRepository.save(cart);
//     }

//     public void clearCart(String userId) {
//         shoppingCartRepository.findByUserId(userId)
//                 .ifPresent(cart -> shoppingCartRepository.delete(cart));
//     }
// }
