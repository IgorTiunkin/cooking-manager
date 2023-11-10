package com.phantom.inventory.models;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_change")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockChange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer changeId;

    @OneToOne
    @JoinColumn (name = "product_id", referencedColumnName = "id")
    private Product product;

    @Column(name = "change")
    private Integer change;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;
}