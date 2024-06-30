package com.bookstore.controller;

import com.bookstore.config.SecurityConfig;
import com.bookstore.model.ShoppingCart;
import com.bookstore.security.JwtTokenProvider;
import com.bookstore.service.ShoppingCartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShoppingCartController.class)
@Import({JwtTokenProvider.class, SecurityConfig.class})  // Import necessary configurations
class ShoppingCartControllerTest {

    private MockMvc mockMvc;


    @MockBean
    private ShoppingCartService shoppingCartService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new ShoppingCartController(shoppingCartService) ).build();
    }

    @Test
    void testGetAllCarts() throws Exception {
        // Arrange
        ShoppingCart cart1 = new ShoppingCart();
        cart1.setUserId("user1");
        ShoppingCart cart2 = new ShoppingCart();
        cart2.setUserId("user2");
        when(shoppingCartService.findAll()).thenReturn(List.of(cart1, cart2));

        // Act and Assert
        mockMvc.perform(get("/cart/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value("user1"))
                .andExpect(jsonPath("$[1].userId").value("user2"));
    }

    @Test
    void testGetCart() throws Exception {
        // Arrange
        String userId = "user1";
        ShoppingCart cart = new ShoppingCart();
        cart.setUserId(userId);
        when(shoppingCartService.getCartByUserId(userId)).thenReturn(cart);

        // Act and Assert
        mockMvc.perform(get("/cart/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId));
    }

    @Test
    void testGetCart_Empty() throws Exception {
        // Arrange
        String userId = "user1";
        ShoppingCart cart = new ShoppingCart();
        cart.setUserId(userId);
        when(shoppingCartService.getCartByUserId(userId)).thenReturn(cart);

        // Act and Assert
        mockMvc.perform(get("/cart/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.items").isEmpty());
    }

    @Test
    void testAddToCart() throws Exception {
        // Arrange
        String userId = "user1";
        ShoppingCart.CartItem item = new ShoppingCart.CartItem();
        item.setBookId("book1");
        item.setQuantity(2);
        ShoppingCart cart = new ShoppingCart();
        cart.setUserId(userId);
        cart.setItems(new ArrayList<>(List.of(item)));
        when(shoppingCartService.addToCart(anyString(), any(ShoppingCart.CartItem.class))).thenReturn(cart);

        // Act and Assert
        mockMvc.perform(post("/cart/{userId}/add", userId)
                .contentType("application/json")
                .content("{\"bookId\": \"book1\", \"quantity\": 2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].bookId").value("book1"))
                .andExpect(jsonPath("$.items[0].quantity").value(2));
    }

    @Test
    void testUpdateItemQuantity() throws Exception {
        // Arrange
        String userId = "user1";
        String bookId = "book1";
        ShoppingCart.CartItem item = new ShoppingCart.CartItem();
        item.setBookId(bookId);
        item.setQuantity(5);
        ShoppingCart cart = new ShoppingCart();
        cart.setUserId(userId);
        cart.setItems(new ArrayList<>(List.of(item)));
        when(shoppingCartService.updateItemQuantity(anyString(), anyString(), anyInt())).thenReturn(cart);

        // Act and Assert
        mockMvc.perform(put("/cart/{userId}/update/{bookId}", userId, bookId)
                .param("quantity", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].quantity").value(5));
    }

    @Test
    void testRemoveFromCart() throws Exception {
        // Arrange
        String userId = "user1";
        String bookId = "book1";
        ShoppingCart cart = new ShoppingCart();
        cart.setUserId(userId);
        when(shoppingCartService.removeFromCart(anyString(), anyString())).thenReturn(cart);

        // Act and Assert
        mockMvc.perform(delete("/cart/{userId}/remove/{bookId}", userId, bookId))
                .andExpect(status().isOk());
    }

    @Test
    void testGetCartItemCount() throws Exception {
        // Arrange
        String userId = "user1";
        when(shoppingCartService.getCartItemCount(userId)).thenReturn(5);

        // Act and Assert
        mockMvc.perform(get("/cart/{userId}/count", userId))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

    @Test
    void testGetCartItemCount_Empty() throws Exception {
        // Arrange
        String userId = "user1";
        when(shoppingCartService.getCartItemCount(userId)).thenReturn(0);

        // Act and Assert
        mockMvc.perform(get("/cart/{userId}/count", userId))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }

    @Test
    void testClearCart() throws Exception {
        // Arrange
        String userId = "user1";

        // Act and Assert
        mockMvc.perform(delete("/cart/{userId}/clear", userId))
                .andExpect(status().isOk());
    }
}
