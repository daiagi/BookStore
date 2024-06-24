package com.bookstore.service;

import com.bookstore.model.ShoppingCart;
import com.bookstore.repository.ShoppingCartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;

    @Autowired
    public ShoppingCartService(ShoppingCartRepository shoppingCartRepository) {
        this.shoppingCartRepository = shoppingCartRepository;
    }

    public List<ShoppingCart> findAll() {
        return shoppingCartRepository.findAll();
    }

    private ShoppingCart getOrCreateCart(String userId) {
        return shoppingCartRepository.findByUserId(userId).orElseGet(() -> {
            ShoppingCart newCart = new ShoppingCart();
            newCart.setUserId(userId);
            return newCart;
        });
    }

    public ShoppingCart getCartByUserId(String userId) {
        return getOrCreateCart(userId);
    }

    public ShoppingCart addToCart(String userId, ShoppingCart.CartItem item) throws IllegalArgumentException {

        if (item.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");

        }

        if (item.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }
        ShoppingCart cart = getOrCreateCart(userId);
        cart.getItems().add(item);
        return shoppingCartRepository.save(cart);
    }

public ShoppingCart updateItemQuantity(String userId, String bookId, int quantity) throws NoSuchElementException, IllegalArgumentException {
    ShoppingCart cart = getCartByUserId(userId);
    if (quantity < 0) {
        throw new IllegalArgumentException("Quantity must be greater than or equal to 0");
    }
    if (quantity == 0) {
        return removeFromCart(userId, bookId);
    }
    ShoppingCart.CartItem item = cart.getItems().stream()
            .filter(cartItem -> cartItem.getBookId().equals(bookId))
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException("Item not found in cart"));
    item.setQuantity(quantity);
    return shoppingCartRepository.save(cart);
}

    public ShoppingCart removeFromCart(String userId, String bookId) {
        ShoppingCart cart = getCartByUserId(userId);
        cart.setItems(cart.getItems().stream()
                .filter(item -> !item.getBookId().equals(bookId))
                .toList());
        return shoppingCartRepository.save(cart);
    }

    public int getCartItemCount(String userId) {
        return shoppingCartRepository.findByUserId(userId)
                .map(cart -> cart.getItems().stream().mapToInt(ShoppingCart.CartItem::getQuantity).sum())
                .orElse(0);
    }

    public void clearCart(String userId) {
        shoppingCartRepository.findByUserId(userId)
                .ifPresent(shoppingCartRepository::delete);
    }
}
