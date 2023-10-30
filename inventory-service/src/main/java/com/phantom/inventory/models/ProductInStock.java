package com.phantom.inventory.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "product_in_stock")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductInStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer stockId;

    @OneToOne
    @JoinColumn (name = "product_id", referencedColumnName = "id")
    private Product product;

    @Column(name = "quantity")
    private Integer quantity;
}
