package com.WhenInRogue.LawnCare99.services.impl;

import com.WhenInRogue.LawnCare99.dtos.MaintenanceRecordDTO;
import com.WhenInRogue.LawnCare99.dtos.Response;
import com.WhenInRogue.LawnCare99.exceptions.NotFoundException;
import com.WhenInRogue.LawnCare99.models.Equipment;
import com.WhenInRogue.LawnCare99.models.MaintenanceRecord;
import com.WhenInRogue.LawnCare99.repositories.EquipmentRepository;
import com.WhenInRogue.LawnCare99.repositories.MaintenanceRecordRepository;
import com.WhenInRogue.LawnCare99.services.MaintenanceRecordService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaintenanceRecordServiceImpl implements MaintenanceRecordService {

    private final MaintenanceRecordRepository maintenanceRecordRepository;
    private final EquipmentRepository equipmentRepository;
    private final ModelMapper modelMapper;

    @Override
    public Response getMaintenanceRecordsByEquipment(Long equipmentId) {

        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new NotFoundException("Equipment not found"));

        List<MaintenanceRecord> records = maintenanceRecordRepository
                .findByEquipment_EquipmentIdOrderByPerformedAtDesc(equipment.getEquipmentId());

        List<MaintenanceRecordDTO> recordDTOs = modelMapper.map(
                records, new TypeToken<List<MaintenanceRecordDTO>>() {
                }.getType());

        recordDTOs.forEach(recordDTO -> {
            if (recordDTO.getEquipment() != null) {
                recordDTO.getEquipment().setMaintenanceDue(null);
            }
        });

        return Response.builder()
                .status(200)
                .message("success")
                .maintenanceRecords(recordDTOs)
                .build();
    }

    @Override
    public Response getAllMaintenanceRecords() {

        List<MaintenanceRecord> records = maintenanceRecordRepository.findAll();
        List<MaintenanceRecordDTO> recordDTOs = modelMapper.map(
                records, new TypeToken<List<MaintenanceRecordDTO>>() {
                }.getType());

        recordDTOs.forEach(recordDTO -> {
            if (recordDTO.getEquipment() != null) {
                recordDTO.getEquipment().setMaintenanceDue(null);
            }
        });

        return Response.builder()
                .status(200)
                .message("success")
                .maintenanceRecords(recordDTOs)
                .build();
    }
}
