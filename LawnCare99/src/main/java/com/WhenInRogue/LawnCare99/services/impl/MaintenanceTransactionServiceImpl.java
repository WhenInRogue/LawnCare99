package com.WhenInRogue.LawnCare99.services.impl;

import com.WhenInRogue.LawnCare99.dtos.MaintenanceRecordDTO;
import com.WhenInRogue.LawnCare99.dtos.MaintenanceTransactionDTO;
import com.WhenInRogue.LawnCare99.dtos.MaintenanceTransactionRequest;
import com.WhenInRogue.LawnCare99.dtos.Response;
import com.WhenInRogue.LawnCare99.enums.EquipmentStatus;
import com.WhenInRogue.LawnCare99.enums.MaintenanceTransactionType;
import com.WhenInRogue.LawnCare99.exceptions.NotFoundException;
import com.WhenInRogue.LawnCare99.models.Equipment;
import com.WhenInRogue.LawnCare99.models.MaintenanceRecord;
import com.WhenInRogue.LawnCare99.models.MaintenanceTransaction;
import com.WhenInRogue.LawnCare99.models.User;
import com.WhenInRogue.LawnCare99.repositories.EquipmentRepository;
import com.WhenInRogue.LawnCare99.repositories.MaintenanceRecordRepository;
import com.WhenInRogue.LawnCare99.repositories.MaintenanceTransactionRepository;
import com.WhenInRogue.LawnCare99.services.MaintenanceTransactionService;
import com.WhenInRogue.LawnCare99.services.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MaintenanceTransactionServiceImpl implements MaintenanceTransactionService {

    private final EquipmentRepository equipmentRepository;
    private final MaintenanceTransactionRepository maintenanceTransactionRepository;
    private final MaintenanceRecordRepository maintenanceRecordRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Override
    public Response startMaintenance(MaintenanceTransactionRequest maintenanceTransactionRequest) {

        Equipment equipment = equipmentRepository.findById(maintenanceTransactionRequest.getEquipmentId())
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

        Double providedHours = maintenanceTransactionRequest.getTotalHoursInput();
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

        User user = userService.getCurrentLoggedInUser();
        MaintenanceTransaction maintenanceTransaction = MaintenanceTransaction.builder()
                .equipment(equipment)
                .user(user)
                .maintenanceTransactionType(MaintenanceTransactionType.START)
                .timestamp(LocalDateTime.now())
                .totalHoursInput(providedHours)
                .note(maintenanceTransactionRequest.getNote())
                .build();

        maintenanceTransactionRepository.save(maintenanceTransaction);

        MaintenanceTransactionDTO maintenanceTransactionDTO = modelMapper.map(maintenanceTransaction, MaintenanceTransactionDTO.class);

        return Response.builder()
                .status(200)
                .message("Maintenance started successfully.")
                .maintenanceTransaction(maintenanceTransactionDTO)
                .build();
    }

    @Override
    public Response completeMaintenance(MaintenanceTransactionRequest maintenanceTransactionRequest) {

        Equipment equipment = equipmentRepository.findById(maintenanceTransactionRequest.getEquipmentId())
                .orElseThrow(() -> new NotFoundException("Equipment Not Found"));

        if (equipment.getEquipmentStatus() != EquipmentStatus.MAINTENANCE) {
            return Response.builder()
                    .status(400)
                    .message("Equipment is not currently under maintenance.")
                    .build();
        }

        Double maintenanceHours = maintenanceTransactionRequest.getTotalHoursInput();
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

        equipment.setTotalHours(maintenanceHours);
        equipment.setLastMaintenanceHours(maintenanceHours);
        equipment.setEquipmentStatus(EquipmentStatus.AVAILABLE);
        equipment.setLastCheckOutTime(null);
        equipmentRepository.save(equipment);

        User user = userService.getCurrentLoggedInUser();
        String maintenancePerformed = maintenanceTransactionRequest.getMaintenancePerformed();
        if (maintenancePerformed == null || maintenancePerformed.trim().isEmpty()) {
            return Response.builder()
                    .status(400)
                    .message("Maintenance performed description is required to complete maintenance.")
                    .build();
        }
        maintenancePerformed = maintenancePerformed.trim();

        MaintenanceRecord maintenanceRecord = MaintenanceRecord.builder()
                .equipment(equipment)
                .user(user)
                .maintenancePerformed(maintenancePerformed)
                .note(maintenanceTransactionRequest.getNote())
                .totalHoursAtMaintenance(maintenanceHours)
                .build();
        maintenanceRecordRepository.save(maintenanceRecord);

        MaintenanceRecordDTO maintenanceRecordDTO = modelMapper.map(maintenanceRecord, MaintenanceRecordDTO.class);

        MaintenanceTransaction maintenanceTransaction = MaintenanceTransaction.builder()
                .equipment(equipment)
                .user(user)
                .maintenanceTransactionType(MaintenanceTransactionType.COMPLETE)
                .timestamp(LocalDateTime.now())
                .totalHoursInput(maintenanceHours)
                .note("Maintenance complete: " + maintenancePerformed)
                .build();

        maintenanceTransactionRepository.save(maintenanceTransaction);

        MaintenanceTransactionDTO maintenanceTransactionDTO = modelMapper.map(maintenanceTransaction, MaintenanceTransactionDTO.class);

        return Response.builder()
                .status(200)
                .message("Maintenance completed successfully.")
                .maintenanceTransaction(maintenanceTransactionDTO)
                .maintenanceRecord(maintenanceRecordDTO)
                .build();
    }

    @Override
    public Response getMaintenanceTransactionsByEquipment(Long equipmentId) {

        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new NotFoundException("Equipment not found"));

        List<MaintenanceTransaction> transactions = maintenanceTransactionRepository
                .findByEquipment_EquipmentIdOrderByTimestampDesc(equipment.getEquipmentId());

        List<MaintenanceTransactionDTO> transactionDTOs = modelMapper.map(
                transactions, new TypeToken<List<MaintenanceTransactionDTO>>() {
                }.getType());

        transactionDTOs.forEach(transactionDTO -> {
            if (transactionDTO.getEquipment() != null) {
                transactionDTO.getEquipment().setMaintenanceDue(null);
            }
        });

        return Response.builder()
                .status(200)
                .message("success")
                .maintenanceTransactions(transactionDTOs)
                .build();
    }
}
