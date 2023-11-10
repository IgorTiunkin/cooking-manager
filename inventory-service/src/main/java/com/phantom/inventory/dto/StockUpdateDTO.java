package com.phantom.inventory.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockUpdateDTO {

    private Integer productId;

    @NotNull(message = "change should not be empty")
    private Integer change;

    private LocalDateTime timestamp;

}
