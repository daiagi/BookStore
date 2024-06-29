package com.bookstore.repository;

import com.bookstore.model.Inventory;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
    Optional<Inventory> findByBookId(Integer bookId);
}
