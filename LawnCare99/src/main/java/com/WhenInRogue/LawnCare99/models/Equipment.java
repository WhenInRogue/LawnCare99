package com.WhenInRogue.LawnCare99.models;

import com.WhenInRogue.LawnCare99.enums.EquipmentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "equipment")
@Data
@Builder
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long equipmentId;

    @NotBlank(message = "Name is required")
    private String name;

    @Min(value = 0, message = "Total Hours cannot be negative")
    private Double totalHours;

    @Enumerated(EnumType.STRING)
    private EquipmentStatus equipmentStatus;

    private LocalDateTime lastCheckOutTime;

    //Set the interval at which that equipment requires maintenance
    @Min(value = 0, message = "maintenance interval cannot be negative")
    private Double maintenanceIntervalHours; // e.g., 500 hours

    //private Double lastMaintenanceHours; // hours recorded when maintenance last completed

    private String description;

    //private user LastCheckedOutBy;???

    @OneToMany(mappedBy = "equipment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MaintenanceRecord> maintenanceRecords;

    /**
     * ❗ Derived field – NOT stored in DB
     *
     * Equipment is due for maintenance if:
     *      totalHours >= maintenanceIntervalHours
     */
    @Transient
    public boolean isMaintenanceDue() {
        if (totalHours == null || maintenanceIntervalHours == null) return false;
        return totalHours >= maintenanceIntervalHours;
    }


    @Override
    public String toString() {
        return "Equipment{" +
                "equipmentId=" + equipmentId +
                ", name='" + name + '\'' +
                ", totalHours=" + totalHours +
                ", equipmentStatus=" + equipmentStatus +
                ", lastCheckOutTime=" + lastCheckOutTime +
                ", maintenanceIntervalHours=" + maintenanceIntervalHours +
                ", description='" + description + '\'' +
                '}';
    }
}
