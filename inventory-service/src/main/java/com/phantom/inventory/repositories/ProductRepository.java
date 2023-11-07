package com.phantom.inventory.repositories;

import com.phantom.inventory.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository <Product, Integer> {
    List<Product> findAllByProductIdIn (List<Integer> list);
}
