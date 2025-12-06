package com.WhenInRogue.LawnCare99.repositories;

import com.WhenInRogue.LawnCare99.models.MaintenanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaintenanceRecordRepository extends JpaRepository<MaintenanceRecord, Long> {

    java.util.List<MaintenanceRecord> findByEquipment_EquipmentIdOrderByPerformedAtDesc(Long equipmentId);

    java.util.List<MaintenanceRecord> findAllByOrderByPerformedAtDesc();
}
