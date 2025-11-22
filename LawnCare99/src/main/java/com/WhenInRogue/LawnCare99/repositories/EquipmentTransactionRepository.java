package com.WhenInRogue.LawnCare99.repositories;

import com.WhenInRogue.LawnCare99.models.EquipmentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EquipmentTransactionRepository extends JpaRepository<EquipmentTransaction, Long>, JpaSpecificationExecutor<EquipmentTransaction> {
}
