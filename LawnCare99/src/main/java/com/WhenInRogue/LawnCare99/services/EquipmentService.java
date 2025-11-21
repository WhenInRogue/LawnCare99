package com.WhenInRogue.LawnCare99.services;

import com.WhenInRogue.LawnCare99.dtos.EquipmentDTO;
import com.WhenInRogue.LawnCare99.dtos.Response;

public interface EquipmentService {

    //Create
    Response createEquipment(EquipmentDTO equipmentDTO);

    //Read
    Response getAllEquipment();

    Response getEquipmentById(Long id);

    //Update
    Response updateEquipment(Long id, EquipmentDTO equipmentDTO);

    //Delete
    Response deleteEquipment(Long id);
}
