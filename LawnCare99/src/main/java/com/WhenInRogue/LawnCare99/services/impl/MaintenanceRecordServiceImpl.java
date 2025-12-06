package com.WhenInRogue.LawnCare99.services.impl;

import com.WhenInRogue.LawnCare99.dtos.MaintenanceRecordDTO;
import com.WhenInRogue.LawnCare99.dtos.MaintenanceRequest;
import com.WhenInRogue.LawnCare99.dtos.Response;
import com.WhenInRogue.LawnCare99.enums.EquipmentStatus;
import com.WhenInRogue.LawnCare99.exceptions.NotFoundException;
import com.WhenInRogue.LawnCare99.models.Equipment;
import com.WhenInRogue.LawnCare99.models.MaintenanceRecord;
import com.WhenInRogue.LawnCare99.models.User;
import com.WhenInRogue.LawnCare99.repositories.EquipmentRepository;
import com.WhenInRogue.LawnCare99.repositories.MaintenanceRecordRepository;
import com.WhenInRogue.LawnCare99.services.MaintenanceRecordService;
import com.WhenInRogue.LawnCare99.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MaintenanceRecordServiceImpl implements MaintenanceRecordService {

    private final MaintenanceRecordRepository maintenanceRecordRepository;
    private final EquipmentRepository equipmentRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Override
    public Response startMaintenance(MaintenanceRequest maintenanceRequest) {

        Equipment equipment = equipmentRepository.findById(maintenanceRequest.getEquipmentId())
                .orElseThrow(() -> new NotFoundException("Equipment Not Found"));

        if (equipment.getEquipmentStatus() == EquipmentStatus.MAINTENANCE) {
            return Response.builder()
                    .status(400)
                    .message("Equipment is already under maintenance.")
                    .build();
        }

        if (equipment.getEquipmentStatus() == EquipmentStatus.IN_USE) {
            return Response.builder()
                    .status(400)
                    .message("Equipment must be checked in before maintenance can start.")
                    .build();
        }

        Double providedHours = maintenanceRequest.getTotalHoursInput();
        if (providedHours != null) {
            double currentHours = equipment.getTotalHours() == null ? 0.0 : equipment.getTotalHours();
            if (providedHours < currentHours) {
                return Response.builder()
                        .status(400)
                        .message("Total hours input cannot be less than the equipment's current recorded hours (" + currentHours + ").")
                        .build();
            }
            equipment.setTotalHours(providedHours);
        }

        equipment.setEquipmentStatus(EquipmentStatus.MAINTENANCE);
        equipment.setLastCheckOutTime(null);
        equipmentRepository.save(equipment);

        return Response.builder()
                .status(200)
                .message("Maintenance started successfully.")
                .build();
    }

    @Override
    public Response endMaintenance(MaintenanceRequest maintenanceRequest) {

        Equipment equipment = equipmentRepository.findById(maintenanceRequest.getEquipmentId())
                .orElseThrow(() -> new NotFoundException("Equipment Not Found"));

        if (equipment.getEquipmentStatus() != EquipmentStatus.MAINTENANCE) {
            return Response.builder()
                    .status(400)
                    .message("Equipment is not currently under maintenance.")
                    .build();
        }

        Double maintenanceHours = maintenanceRequest.getTotalHoursInput();
        if (maintenanceHours == null) {
            maintenanceHours = equipment.getTotalHours();
        }

        if (maintenanceHours == null) {
            return Response.builder()
                    .status(400)
                    .message("Total hours input is required to complete maintenance.")
                    .build();
        }

        if (maintenanceHours < 0) {
            return Response.builder()
                    .status(400)
                    .message("Total hours input must be non-negative.")
                    .build();
        }

        double currentHours = equipment.getTotalHours() == null ? 0.0 : equipment.getTotalHours();
        if (currentHours != 0.0 && maintenanceHours < currentHours) {
            return Response.builder()
                    .status(400)
                    .message("Total hours input cannot be less than the equipment's current recorded hours (" + currentHours + ").")
                    .build();
        }

        double previousMaintenanceHours = equipment.getLastMaintenanceHours() == null ? 0.0 : equipment.getLastMaintenanceHours();
        if (maintenanceHours < previousMaintenanceHours) {
            return Response.builder()
                    .status(400)
                    .message("Total hours input cannot be less than the last recorded maintenance hours (" + previousMaintenanceHours + ").")
                    .build();
        }

        String maintenancePerformed = maintenanceRequest.getMaintenancePerformed();
        if (maintenancePerformed == null || maintenancePerformed.trim().isEmpty()) {
            return Response.builder()
                    .status(400)
                    .message("Maintenance performed description is required to complete maintenance.")
                    .build();
        }
        maintenancePerformed = maintenancePerformed.trim();

        equipment.setTotalHours(maintenanceHours);
        equipment.setLastMaintenanceHours(maintenanceHours);
        equipment.setEquipmentStatus(EquipmentStatus.AVAILABLE);
        equipment.setLastCheckOutTime(null);
        equipmentRepository.save(equipment);

        User user = userService.getCurrentLoggedInUser();

        MaintenanceRecord maintenanceRecord = MaintenanceRecord.builder()
                .equipment(equipment)
                .user(user)
                .maintenancePerformed(maintenancePerformed)
                .note(maintenanceRequest.getNote())
                .totalHoursAtMaintenance(maintenanceHours)
                .build();
        maintenanceRecordRepository.save(maintenanceRecord);

        MaintenanceRecordDTO maintenanceRecordDTO = modelMapper.map(maintenanceRecord, MaintenanceRecordDTO.class);

        return Response.builder()
                .status(200)
                .message("Maintenance completed successfully.")
                .maintenanceRecord(maintenanceRecordDTO)
                .build();
    }

    @Override
    public Response getMaintenanceRecordsByEquipment(Long equipmentId) {

        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new NotFoundException("Equipment not found"));

        List<MaintenanceRecord> records = maintenanceRecordRepository
                .findByEquipment_EquipmentIdOrderByPerformedAtDesc(equipment.getEquipmentId());

        List<MaintenanceRecordDTO> maintenanceRecordDTOS = modelMapper.map(
                records, new TypeToken<List<MaintenanceRecordDTO>>() {
                }.getType());

        maintenanceRecordDTOS.forEach(maintenanceRecordDTO -> {
            if (maintenanceRecordDTO.getEquipment() != null) {
                maintenanceRecordDTO.getEquipment().setMaintenanceDue(null);
            }
            if (maintenanceRecordDTO.getUser() != null) {
                maintenanceRecordDTO.getUser().setMaintenanceRecords(null);
                maintenanceRecordDTO.getUser().setEquipmentTransactions(null);
                maintenanceRecordDTO.getUser().setSupplyTransactions(null);
            }
        });

        return Response.builder()
                .status(200)
                .message("success")
                .maintenanceRecords(maintenanceRecordDTOS)
                .build();
    }

    @Override
    public Response getAllMaintenanceRecords() {
        List<MaintenanceRecord> records = maintenanceRecordRepository.findAllByOrderByPerformedAtDesc();
        List<MaintenanceRecordDTO> maintenanceRecordDTOS = modelMapper.map(
                records, new TypeToken<List<MaintenanceRecordDTO>>() {
                }.getType());

        maintenanceRecordDTOS.forEach(maintenanceRecordDTO -> {
            if (maintenanceRecordDTO.getEquipment() != null) {
                maintenanceRecordDTO.getEquipment().setMaintenanceDue(null);
            }
            if (maintenanceRecordDTO.getUser() != null) {
                maintenanceRecordDTO.getUser().setMaintenanceRecords(null);
                maintenanceRecordDTO.getUser().setEquipmentTransactions(null);
                maintenanceRecordDTO.getUser().setSupplyTransactions(null);
            }
        });

        return Response.builder()
                .status(200)
                .message("success")
                .maintenanceRecords(maintenanceRecordDTOS)
                .build();
    }

}
