package com.WhenInRogue.LawnCare99.dtos;

import com.WhenInRogue.LawnCare99.enums.MaintenanceTransactionType;
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
public class MaintenanceTransactionDTO {

    private Long maintenanceTransactionId;
    private Double totalHoursInput;
    private String note;
    private MaintenanceTransactionType maintenanceTransactionType;
    private LocalDateTime timestamp;
    private EquipmentDTO equipment;
    private UserDTO user;
}
