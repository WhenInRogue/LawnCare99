package com.WhenInRogue.LawnCare99.dtos;

import com.WhenInRogue.LawnCare99.enums.SupplyTransactionType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SupplyTransactionDTO {

    private Long supplyTransactionId;

    private Integer quantity;

    private String note;

    private SupplyTransactionType supplyTransactionType; //check-in, check-out

    private final LocalDateTime createdAt = LocalDateTime.now();

    private SupplyDTO supply;
    private UserDTO user;

}
