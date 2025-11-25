package com.WhenInRogue.LawnCare99.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EquipmentTransactionRequest {

    @Positive(message = "equipment id is required")
    private Long equipmentId;

    private Double totalHoursInput;

    private String note;
}
