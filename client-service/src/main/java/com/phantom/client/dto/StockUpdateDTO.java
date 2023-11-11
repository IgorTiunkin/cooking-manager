package com.phantom.client.dto;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockUpdateDTO {

    private Integer productId;

    @NotNull(message = "Quantity should not be empty")
    @Min(value = -1000000000, message = "quantity should not be less than -1000000000")
    @Max(value = 1000000000, message = "Quantity should not be over 1000000000")
    private Integer change;

    private LocalDateTime timestamp;

}
