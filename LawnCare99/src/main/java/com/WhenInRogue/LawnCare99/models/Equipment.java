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

    private String description;

    //private user LastCheckedOutBy;???


    @Override
    public String toString() {
        return "Equipment{" +
                "equipmentId=" + equipmentId +
                ", name='" + name + '\'' +
                ", totalHours=" + totalHours +
                ", equipmentStatus=" + equipmentStatus +
                ", lastCheckOutTime=" + lastCheckOutTime +
                ", description='" + description + '\'' +
                '}';
    }
}
