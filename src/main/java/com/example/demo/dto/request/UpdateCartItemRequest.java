package com.example.demo.dto.request;

import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCartItemRequest {
    @Min(value = 0, message = "Số lượng phải >= 0")
    private int quantity;

}

