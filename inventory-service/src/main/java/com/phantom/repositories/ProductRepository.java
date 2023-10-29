package com.phantom.repositories;

import com.phantom.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository <Product, Integer> {
    List<Product> findAllByProductIdIn (List <Integer> list);
}
