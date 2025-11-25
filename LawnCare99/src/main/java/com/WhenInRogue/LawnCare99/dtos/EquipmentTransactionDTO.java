package com.WhenInRogue.LawnCare99.dtos;

import com.WhenInRogue.LawnCare99.enums.EquipmentTransactionType;
import com.WhenInRogue.LawnCare99.models.Equipment;
import com.WhenInRogue.LawnCare99.models.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EquipmentTransactionDTO {

    private Long equipmentTransactionId;

    private Double hoursLogged;

    private String note;

    private EquipmentTransactionType equipmentTransactionType;

    private LocalDateTime timestamp = LocalDateTime.now();

    private Double totalHoursInput;

    private Equipment equipment;
    private User user;
}
