package com.WhenInRogue.LawnCare99.models;

import com.WhenInRogue.LawnCare99.enums.SupplyTransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "supply_transactions")
@Data
@Builder
public class SupplyTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long supplyTransactionId;

    private Integer quantity;

    private String note;

    @Enumerated(EnumType.STRING)
    private SupplyTransactionType supplyTransactionType; //check-in, check-out

    private final LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supply_id")
    private Supply supply;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") //does user_id need to be just id?
    private User user;


    @Override
    public String toString() {
        return "SupplyTransaction{" +
                "supplyTransactionId=" + supplyTransactionId +
                ", quantity=" + quantity +
                ", note='" + note + '\'' +
                ", supplyTransactionType=" + supplyTransactionType +
                ", createdAt=" + createdAt +
                '}';
    }
}
