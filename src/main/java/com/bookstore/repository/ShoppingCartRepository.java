package com.bookstore.repository;

import com.bookstore.model.ShoppingCart;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ShoppingCartRepository extends MongoRepository<ShoppingCart, String> {
    Optional<ShoppingCart> findByUserId(String userId);

    @Query("{'items.bookId': ?0}")
    List<ShoppingCart> findByBookIdInItems(String bookId);

}
