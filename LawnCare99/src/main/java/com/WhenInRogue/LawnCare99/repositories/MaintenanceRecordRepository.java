package com.WhenInRogue.LawnCare99.repositories;

import com.WhenInRogue.LawnCare99.models.MaintenanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaintenanceRecordRepository extends JpaRepository<MaintenanceRecord, Long> {

    java.util.List<MaintenanceRecord> findByEquipment_EquipmentIdOrderByPerformedAtDesc(Long equipmentId);
}
