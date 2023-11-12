package com.phantom.inventory.models;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "product_in_stock")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @Column(name = "recommended_quantity")
    private Integer recommendedQuantity;
}
