package com.WhenInRogue.LawnCare99.repositories;

import com.WhenInRogue.LawnCare99.enums.EquipmentStatus;
import com.WhenInRogue.LawnCare99.models.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    List<Equipment> findByNameContainingOrDescriptionContaining(String name, String description);
    List<Equipment> findByEquipmentStatus(EquipmentStatus equipmentStatus);
}
