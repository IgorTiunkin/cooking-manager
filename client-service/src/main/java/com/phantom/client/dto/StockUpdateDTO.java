package com.phantom.client.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockUpdateDTO {

    private Integer productId;

    private Integer change;

    private LocalDateTime timestamp;

}
