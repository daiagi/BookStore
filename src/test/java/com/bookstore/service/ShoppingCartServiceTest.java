package com.bookstore.service;

import com.bookstore.model.ShoppingCart;
import com.bookstore.repository.ShoppingCartRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ShoppingCartServiceTest {

    private ShoppingCart cart;
    private String userId;

    @InjectMocks
    private ShoppingCartService shoppingCartService;

    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        userId = "user1";
        cart = new ShoppingCart(userId);
        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(shoppingCartRepository.save(any(ShoppingCart.class))).thenReturn(cart);

    }

    @Test
    void testFindAll() {
        // Arrange
        ShoppingCart cart1 = new ShoppingCart("user1");
        ShoppingCart cart2 = new ShoppingCart("user2");
        when(shoppingCartRepository.findAll()).thenReturn(List.of(cart1, cart2));

        // Act
        List<ShoppingCart> result = shoppingCartService.findAll();

        // Assert
        assertEquals(2, result.size());
        verify(shoppingCartRepository, times(1)).findAll();
    }

    @Test
    void testGetCartByUserId_NewCart() {
        // Arrange
        String userId = "newUser";
        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // Act
        ShoppingCart result = shoppingCartService.getCartByUserId(userId);

        // Assert
        assertEquals(userId, result.getUserId());
        verify(shoppingCartRepository, times(1)).findByUserId(userId);
    }

    @Test
    void testGetCartByUserId_ExistingCart() {
        // Arrange
        String userId = "existingUser";
        ShoppingCart cart = new ShoppingCart(userId);
        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        // Act
        ShoppingCart result = shoppingCartService.getCartByUserId(userId);

        // Assert
        assertEquals(userId, result.getUserId());
        verify(shoppingCartRepository, times(1)).findByUserId(userId);
    }

    @Test
    void testAddToCart() {
        // Arrange
        ShoppingCart.CartItem item = new ShoppingCart.CartItem("book1", 2, 10.0);

        // Act
        ShoppingCart result = shoppingCartService.addToCart(this.userId, item);

        // Assert
        assertEquals(1, result.getItems().size());
        assertEquals("book1", result.getItems().get(0).getBookId());
        verify(shoppingCartRepository, times(1)).save(any(ShoppingCart.class));
    }

    @Test
    void testAddToCart_ZeroQuantity() {
        // Arrange
        ShoppingCart.CartItem item = new ShoppingCart.CartItem("book1", 0, 10.0);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> shoppingCartService.addToCart(userId, item));
    }

    @Test
    void testAddToCart_NegativeQuantity() {
        // Arrange
        ShoppingCart.CartItem item = new ShoppingCart.CartItem("book1", -1, 10.0);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> shoppingCartService.addToCart(userId, item));
    }

    @Test
    void testAddToCart_ZeroPrice() {
        // Arrange
        ShoppingCart.CartItem item = new ShoppingCart.CartItem("book1", 1, 0.0);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> shoppingCartService.addToCart(userId, item));
    }

    @Test
    void testAddToCart_NegativePrice() {
        // Arrange
        ShoppingCart.CartItem item = new ShoppingCart.CartItem("book1", 1, -10.0);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> shoppingCartService.addToCart(userId, item));
    }

    @Test

    void testUpdateItemQuantity_NegativeQuantity() {
        // Arrange
        String bookId = "book1";
        ShoppingCart.CartItem item = new ShoppingCart.CartItem(bookId, 2, 10.0);

        shoppingCartService.addToCart(userId, item);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> shoppingCartService.updateItemQuantity(userId, bookId, -1));
    }

    @Test
    void testUpdateItemQuantity_ZeroQuantity() {
        // Arrange
        String bookId = "book1";
        ShoppingCart.CartItem item = new ShoppingCart.CartItem(bookId, 2, 10.0);

        shoppingCartService.addToCart(userId, item);

        // Act
        ShoppingCart result = shoppingCartService.updateItemQuantity(userId, bookId, 0);

        // Assert
        assertEquals(0, result.getItems().size());
        verify(shoppingCartRepository, times(2)).save(any(ShoppingCart.class));
    }

    // test for updating quantity but the item is not in the cart

    @Test
    void testUpdateItemQuantity_ItemNotInCart() {

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> shoppingCartService.updateItemQuantity(userId, "book1", 5));
    }

    @Test
    void testUpdateItemQuantity() {
        // Arrange
        String bookId = "book1";
        ShoppingCart.CartItem item = new ShoppingCart.CartItem(bookId, 2, 10.0);

        shoppingCartService.addToCart(userId, item);

        // Act
        ShoppingCart result = shoppingCartService.updateItemQuantity(userId, bookId, 5);

        // Assert
        assertEquals(1, result.getItems().size());
        assertEquals(5, result.getItems().get(0).getQuantity());
        verify(shoppingCartRepository, times(2)).save(any(ShoppingCart.class));
    }

    @Test
    void testRemoveFromCart() {
        // Arrange
        String bookId = "book1";
        ShoppingCart.CartItem item = new ShoppingCart.CartItem(bookId, 2, 10.0);

        shoppingCartService.addToCart(userId, item);

        // Act
        ShoppingCart result = shoppingCartService.removeFromCart(userId, bookId);

        // Assert
        assertEquals(0, result.getItems().size());
        verify(shoppingCartRepository, times(2)).save(any(ShoppingCart.class));
    }

    @Test
    void testRemoveFromCart_itemIsNotInCart() {
        // Arrange
        String bookId = "book1";

        shoppingCartService.addToCart(userId, new ShoppingCart.CartItem("book2", 2, 10.0));

        // Act
        ShoppingCart result = shoppingCartService.removeFromCart(userId, bookId);

        // Assert
        assertEquals(1, result.getItems().size());
        verify(shoppingCartRepository, times(2)).save(any(ShoppingCart.class));
    }

    @Test
    void testGetCartItemCount() {
        // Arrange
        ShoppingCart.CartItem item1 = new ShoppingCart.CartItem("book1", 2, 10.0);

        ShoppingCart.CartItem item2 = new ShoppingCart.CartItem("book2", 3, 10.0);
        cart.setItems(new ArrayList<>(List.of(item1, item2)));

        // Act
        int itemCount = shoppingCartService.getCartItemCount(userId);

        // Assert
        assertEquals(5, itemCount);
        verify(shoppingCartRepository, times(1)).findByUserId(userId);
    }

    @Test
    void testGetCartItemCount_EmptyCart() {

        // Act
        int itemCount = shoppingCartService.getCartItemCount(userId);

        // Assert
        assertEquals(0, itemCount);
        verify(shoppingCartRepository, times(1)).findByUserId(userId);
    }

    @Test
    void testClearCart() {

        // Act
        shoppingCartService.clearCart(userId);

        // Assert
        verify(shoppingCartRepository, times(1)).findByUserId(userId);
        verify(shoppingCartRepository, times(1)).delete(cart);
    }
}
