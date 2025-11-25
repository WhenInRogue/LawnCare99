package com.WhenInRogue.LawnCare99.dtos;

import com.WhenInRogue.LawnCare99.enums.EquipmentStatus;
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
public class EquipmentDTO {

    private Long equipmentId;

    private String name;

    private Double totalHours;

    private EquipmentStatus equipmentStatus; //AVAILABLE, IN_USE, MAINTENANCE, RETIRED

    private LocalDateTime lastCheckOutTime;

    private Double maintenanceIntervalHours;

    private String description;

    private Boolean maintenanceDue;

    public Boolean getMaintenanceDue() {
        if (totalHours == null || maintenanceIntervalHours == null) {
            return false;
        }
        return totalHours >= maintenanceIntervalHours;
    }
}
