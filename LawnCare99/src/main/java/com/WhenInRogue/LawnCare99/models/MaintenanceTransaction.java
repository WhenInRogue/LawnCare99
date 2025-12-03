package com.WhenInRogue.LawnCare99.models;

import com.WhenInRogue.LawnCare99.enums.MaintenanceTransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "maintenance_transactions")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MaintenanceTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maintenanceTransactionId;

    @Min(value = 0, message = "Value must be non-negative")
    private Double totalHoursInput;

    private String note;

    @Enumerated(EnumType.STRING)
    private MaintenanceTransactionType maintenanceTransactionType;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
