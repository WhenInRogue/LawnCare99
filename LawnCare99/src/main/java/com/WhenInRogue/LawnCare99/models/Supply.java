package com.WhenInRogue.LawnCare99.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "supplies")
@Data
@Builder
public class Supply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long supplyId;

    @NotBlank(message = "Name is required")
    private String name;

    //measurement unit eg. (gallons, boxes) etc.
    private String unitOfMeasurement;

    @Min(value = 0, message = "current stock cannot be negative")
    private Integer currentStock;

    @Min(value = 0, message = "reorder level cannot be negative")
    private Integer reorderLevel;

    //@NotNull is for Integer @NotBlank is for strings
    @NotNull(message = "You must set a value")
    @Positive(message = "Maximum Quantity cannot be less than 1")
    private Integer maximumQuantity;


    private String description;


    @Override
    public String toString() {
        return "Supply{" +
                "supplyId=" + supplyId +
                ", name='" + name + '\'' +
                ", unitOfMeasurement='" + unitOfMeasurement + '\'' +
                ", currentStock=" + currentStock +
                ", reorderLevel=" + reorderLevel +
                ", maximumQuantity=" + maximumQuantity +
                ", description='" + description + '\'' +
                '}';
    }
}
