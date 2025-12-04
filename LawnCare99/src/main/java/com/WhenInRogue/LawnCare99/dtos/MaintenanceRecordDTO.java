package com.WhenInRogue.LawnCare99.dtos;

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
public class MaintenanceRecordDTO {

    private Long maintenanceRecordId;
    private String maintenancePerformed;
    private String note;
    private Double totalHoursAtMaintenance;
    private LocalDateTime performedAt;

    private EquipmentDTO equipment;
    private UserDTO user;
}
