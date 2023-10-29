package com.phantom.dto;

import com.phantom.models.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductInStockDTO {

    private int stockId;

    private int quantity;
}
