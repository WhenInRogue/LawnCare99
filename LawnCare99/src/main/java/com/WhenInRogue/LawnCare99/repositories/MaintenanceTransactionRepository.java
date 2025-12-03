package com.WhenInRogue.LawnCare99.repositories;

import com.WhenInRogue.LawnCare99.models.MaintenanceTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceTransactionRepository extends JpaRepository<MaintenanceTransaction, Long> {

    List<MaintenanceTransaction> findByEquipment_EquipmentIdOrderByTimestampDesc(Long equipmentId);
}
