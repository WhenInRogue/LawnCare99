package com.WhenInRogue.LawnCare99.services;

import com.WhenInRogue.LawnCare99.dtos.EquipmentDTO;
import com.WhenInRogue.LawnCare99.dtos.Response;
import com.WhenInRogue.LawnCare99.enums.EquipmentStatus;

public interface EquipmentService {

    //Create
    Response createEquipment(EquipmentDTO equipmentDTO);

    //Read
    Response getAllEquipment(EquipmentStatus equipmentStatus);

    Response getEquipmentById(Long id);

    //Update
    Response updateEquipment(Long id, EquipmentDTO equipmentDTO);

    //Delete
    Response deleteEquipment(Long id);
}
