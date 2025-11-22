package com.WhenInRogue.LawnCare99.models;

import com.WhenInRogue.LawnCare99.enums.EquipmentTransactionType;
import jakarta.persistence.*;
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

    //Used only in Check-in
    private Double hoursLogged;

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
                ", note='" + note + '\'' +
                ", equipmentTransactionType=" + equipmentTransactionType +
                ", timestamp=" + timestamp +
                '}';
    }
}
