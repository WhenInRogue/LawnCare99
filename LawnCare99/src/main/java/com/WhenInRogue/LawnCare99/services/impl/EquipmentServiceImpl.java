package com.WhenInRogue.LawnCare99.services.impl;

import com.WhenInRogue.LawnCare99.dtos.EquipmentDTO;
import com.WhenInRogue.LawnCare99.dtos.Response;
import com.WhenInRogue.LawnCare99.enums.EquipmentStatus;
import com.WhenInRogue.LawnCare99.exceptions.NotFoundException;
import com.WhenInRogue.LawnCare99.models.Equipment;
import com.WhenInRogue.LawnCare99.repositories.EquipmentRepository;
import com.WhenInRogue.LawnCare99.services.EquipmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EquipmentServiceImpl implements EquipmentService {

    private final ModelMapper modelMapper;
    private final EquipmentRepository equipmentRepository;

    @Override
    public Response createEquipment(EquipmentDTO equipmentDTO) {

        Equipment equipmentToSave = modelMapper.map(equipmentDTO, Equipment.class);

        // Force newly created equipment to be AVAILABLE regardless of DTO contents
        equipmentToSave.setEquipmentStatus(EquipmentStatus.AVAILABLE);

        equipmentRepository.save(equipmentToSave);

        return Response.builder()
                .status(200)
                .message("Equipment created successfully")
                .build();
    }

    @Override
    public Response getAllEquipment(EquipmentStatus equipmentStatus) {

        List<Equipment> equipments;

        if (equipmentStatus == null) {
            equipments = equipmentRepository.findAll(Sort.by(Sort.Direction.DESC, "equipmentId"));
        } else {
            equipments = equipmentRepository.findByEquipmentStatus(equipmentStatus);
            equipments.sort(Comparator.comparing(Equipment::getEquipmentId).reversed());
        }

        List<EquipmentDTO> EquipmentDTOList = modelMapper.map(equipments, new TypeToken<List<EquipmentDTO>>() {
        }.getType());

        return Response.builder()
                .status(200)
                .message("success")
                .equipments(EquipmentDTOList)
                .build();
    }

    @Override
    public Response getEquipmentById(Long id) {

        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Equipment not found"));

        EquipmentDTO equipmentDTO = modelMapper.map(equipment, EquipmentDTO.class);

        return Response.builder()
                .status(200)
                .message("success")
                .equipment(equipmentDTO)
                .build();
    }

    @Override
    public Response updateEquipment(Long id, EquipmentDTO equipmentDTO) {

        Equipment existingEquipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Equipment not found"));

        if  (equipmentDTO.getName() != null) existingEquipment.setName(equipmentDTO.getName());
        if (equipmentDTO.getTotalHours() != null) existingEquipment.setTotalHours(equipmentDTO.getTotalHours());
        if (equipmentDTO.getEquipmentStatus() != null) existingEquipment.setEquipmentStatus(equipmentDTO.getEquipmentStatus());
        if (equipmentDTO.getLastCheckOutTime() != null) existingEquipment.setLastCheckOutTime(equipmentDTO.getLastCheckOutTime());
        if (equipmentDTO.getLastCheckedOutBy() != null) existingEquipment.setLastCheckedOutBy(equipmentDTO.getLastCheckedOutBy());
        if (equipmentDTO.getMaintenanceIntervalHours() != null) existingEquipment.setMaintenanceIntervalHours(equipmentDTO.getMaintenanceIntervalHours());
        if  (equipmentDTO.getDescription() != null) existingEquipment.setDescription(equipmentDTO.getDescription());
        if (equipmentDTO.getLastMaintenanceHours() != null) existingEquipment.setLastMaintenanceHours(equipmentDTO.getLastMaintenanceHours());

        equipmentRepository.save(existingEquipment);

        return Response.builder()
                .status(200)
                .message("Equipment updated successfully")
                .build();

    }

    @Override
    public Response deleteEquipment(Long id) {
        equipmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Equipment not found"));

        equipmentRepository.deleteById(id);

        return Response.builder()
                .status(200)
                .message("Equipment deleted successfully")
                .build();
    }
}
