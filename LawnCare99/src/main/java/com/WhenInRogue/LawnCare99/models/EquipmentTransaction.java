package com.WhenInRogue.LawnCare99.models;

import com.WhenInRogue.LawnCare99.enums.EquipmentTransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "equipment_transactions")
@Data
@Builder
public class EquipmentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long equipmentTransactionId;

    /**
     * System-calculated hours of use.
     * Only populated for CHECK_IN transactions.
     */
    private Double hoursLogged;

    /**
     * The hours the USER entered at the moment of the transaction.
     * (This is the odometer-style reading from the equipment)
     */
    @Min(value = 0, message = "Value must be non-negative")
    private Double totalHoursInput;

    private String note;

    @Enumerated(EnumType.STRING)
    private EquipmentTransactionType equipmentTransactionType;

    private LocalDateTime timestamp = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    //OneToOne
    //private MaintenanceRecord maintenanceRecord;


    @Override
    public String toString() {
        return "EquipmentTransaction{" +
                "equipmentTransactionId=" + equipmentTransactionId +
                ", hoursLogged=" + hoursLogged +
                ", totalHoursInput=" + totalHoursInput +
                ", note='" + note + '\'' +
                ", equipmentTransactionType=" + equipmentTransactionType +
                ", timestamp=" + timestamp +
                '}';
    }
}
