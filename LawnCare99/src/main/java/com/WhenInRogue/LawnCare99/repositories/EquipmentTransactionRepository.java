package com.WhenInRogue.LawnCare99.repositories;

import com.WhenInRogue.LawnCare99.enums.EquipmentTransactionType;
import com.WhenInRogue.LawnCare99.models.EquipmentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface EquipmentTransactionRepository extends JpaRepository<EquipmentTransaction, Long>, JpaSpecificationExecutor<EquipmentTransaction> {
    Optional<EquipmentTransaction> findTopByEquipment_EquipmentIdAndEquipmentTransactionTypeOrderByTimestampDesc(
            Long equipmentId,
            EquipmentTransactionType equipmentTransactionType
    );
}
