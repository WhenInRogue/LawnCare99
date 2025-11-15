package com.WhenInRogue.LawnCare99.repositories;

import com.WhenInRogue.LawnCare99.models.SupplyTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SupplyTransactionRepository extends JpaRepository<SupplyTransaction, Long>, JpaSpecificationExecutor<SupplyTransaction> {
}
