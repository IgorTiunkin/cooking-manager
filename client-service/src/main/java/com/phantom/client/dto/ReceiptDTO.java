package com.phantom.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptDTO {

    private String title;

    private Map<ProductDTO, Integer> usedProducts = new HashMap<>();//todo convert to linked map

    private String actions;

    @Override
    public String toString() {
        return "ReceiptDTO{" +
                "title='" + title + '\'' +
                ", usedProducts=" + usedProducts +
                ", actions='" + actions + '\'' +
                '}';
    }
}
