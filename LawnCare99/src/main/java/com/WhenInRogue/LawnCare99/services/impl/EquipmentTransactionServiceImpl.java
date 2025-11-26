package com.WhenInRogue.LawnCare99.services.impl;

import com.WhenInRogue.LawnCare99.dtos.EquipmentTransactionDTO;
import com.WhenInRogue.LawnCare99.dtos.EquipmentTransactionRequest;
import com.WhenInRogue.LawnCare99.dtos.Response;
import com.WhenInRogue.LawnCare99.enums.EquipmentStatus;
import com.WhenInRogue.LawnCare99.enums.EquipmentTransactionType;
import com.WhenInRogue.LawnCare99.exceptions.NotFoundException;
import com.WhenInRogue.LawnCare99.models.Equipment;
import com.WhenInRogue.LawnCare99.models.EquipmentTransaction;
import com.WhenInRogue.LawnCare99.models.User;
import com.WhenInRogue.LawnCare99.repositories.EquipmentRepository;
import com.WhenInRogue.LawnCare99.repositories.EquipmentTransactionRepository;
import com.WhenInRogue.LawnCare99.services.EquipmentTransactionService;
import com.WhenInRogue.LawnCare99.services.UserService;
import com.WhenInRogue.LawnCare99.specification.EquipmentTransactionFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EquipmentTransactionServiceImpl implements EquipmentTransactionService {

    private final EquipmentTransactionRepository equipmentTransactionRepository;
    private final EquipmentRepository equipmentRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Override
    public Response checkInEquipment(EquipmentTransactionRequest equipmentTransactionRequest) {

        Long equipmentId = equipmentTransactionRequest.getEquipmentId();
        Double totalHoursInput = equipmentTransactionRequest.getTotalHoursInput();

        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new NotFoundException("Equipment Not Found"));

        User user = userService.getCurrentLoggedInUser();

        // VALIDATION -----------------------------------
        if (equipment.getEquipmentStatus() != EquipmentStatus.IN_USE) {
            return Response.builder()
                    .status(400)
                    .message("Equipment is not currently checked out.")
                    .build();
        }

        if (totalHoursInput == null) {
            return Response.builder()
                    .status(400)
                    .message("Total hours input is required to check in equipment.")
                    .build();
        }

        //Find the most recent Check out transaction
        EquipmentTransaction lastCheckout = equipmentTransactionRepository
                .findTopByEquipment_EquipmentIdAndEquipmentTransactionTypeOrderByTimestampDesc(
                        equipmentId, EquipmentTransactionType.CHECK_OUT)
                .orElseThrow(() -> new RuntimeException("No checkout record found."));

        // Prevent rollback: check-in reading must be >= checkout reading
        double lastCheckoutHours = lastCheckout.getTotalHoursInput();
        if (totalHoursInput < lastCheckoutHours) {
            return Response.builder()
                    .status(400)
                    .message("Total hours input cannot be less than the last checkout reading ("
                            + lastCheckoutHours + ").")
                    .build();
        }

        // CALCULATE HOURS -------------------------------
        double hoursUsed = totalHoursInput - lastCheckout.getTotalHoursInput();
        if (hoursUsed < 0) hoursUsed = 0;

        // UPDATE TOTAL HOURS ----------------------------
        double previousTotalHours = equipment.getTotalHours() == null ? 0.0 : equipment.getTotalHours();
        double newTotalHours = previousTotalHours + hoursUsed;
        equipment.setTotalHours(newTotalHours);

        equipment.setEquipmentStatus(EquipmentStatus.AVAILABLE);
        equipment.setLastCheckOutTime(null);
        equipmentRepository.save(equipment);

        // LOG TRANSACTION -------------------------------
        EquipmentTransaction equipmentTransaction = EquipmentTransaction.builder()
                .equipment(equipment)
                .user(user)
                .equipmentTransactionType(EquipmentTransactionType.CHECK_IN)
                .timestamp(LocalDateTime.now())
                .totalHoursInput(equipmentTransactionRequest.getTotalHoursInput())
                .hoursLogged(hoursUsed)
                .note(equipmentTransactionRequest.getNote())
                .build();

        equipmentTransactionRepository.save(equipmentTransaction);

        return Response.builder()
                .status(200)
                .message("Equipment checked in successfully")
                .build();
    }

    @Override
    public Response checkOutEquipment(EquipmentTransactionRequest equipmentTransactionRequest) {

        Long equipmentId = equipmentTransactionRequest.getEquipmentId();

        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new NotFoundException("Equipment Not Found"));

        User user = userService.getCurrentLoggedInUser();

        // VALIDATION -----------------------------------
        if (equipment.getEquipmentStatus() == EquipmentStatus.MAINTENANCE) {
            return Response.builder()
                    .status(400)
                    .message("Equipment is under maintenance and cannot be checked out.")
                    .build();
        }

        if (equipment.getEquipmentStatus() == EquipmentStatus.IN_USE) {
            return Response.builder()
                    .status(400)
                    .message("Equipment is already checked out.")
                    .build();
        }

        // Prevent hour meter rollback
        double previousTotalHours = equipment.getTotalHours() == null ? 0.0 : equipment.getTotalHours();

        if ((equipmentTransactionRequest.getTotalHoursInput()) < previousTotalHours) {
            return Response.builder()
                    .status(400)
                    .message("Total hours input cannot be less than the equipment's current recorded hours ("
                            + previousTotalHours + ").")
                    .build();
        }

        // UPDATE EQUIPMENT ------------------------------
        equipment.setEquipmentStatus(EquipmentStatus.IN_USE);
        equipment.setLastCheckOutTime(LocalDateTime.now());
        equipmentRepository.save(equipment);

        // LOG TRANSACTION -------------------------------
        EquipmentTransaction equipmentTransaction = EquipmentTransaction.builder()
                .equipment(equipment)
                .user(user)
                .equipmentTransactionType(EquipmentTransactionType.CHECK_OUT)
                .timestamp(LocalDateTime.now())
                .totalHoursInput(equipmentTransactionRequest.getTotalHoursInput())
                .note(equipmentTransactionRequest.getNote())
                .build();

        equipmentTransactionRepository.save(equipmentTransaction);

        return Response.builder()
                .status(200)
                .message("Equipment checked out successfully")
                .build();
    }

    @Override
    public Response getAllEquipmentTransactions(int page, int size, String filter) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "equipmentTransactionId"));

        //user the Transaction specification
        Specification<EquipmentTransaction> spec = EquipmentTransactionFilter.equipmentTransactionFilter(filter);
        Page<EquipmentTransaction> equipmentTransactionPage = equipmentTransactionRepository.findAll(spec, pageable);

        List<EquipmentTransactionDTO> equipmentTransactionDTOS = modelMapper.map(equipmentTransactionPage.getContent(), new TypeToken<List<EquipmentTransactionDTO>>() {
        }.getType());

        equipmentTransactionDTOS.forEach(equipmentTransactionDTO -> {
            equipmentTransactionDTO.setUser(null);
            equipmentTransactionDTO.setEquipment(null);
        });

        return Response.builder()
                .status(200)
                .message("success")
                .equipmentTransactions(equipmentTransactionDTOS)
                .totalElements(equipmentTransactionPage.getTotalElements())
                .totalPages(equipmentTransactionPage.getTotalPages())
                .build();
    }

    @Override
    public Response getAllEquipmentTransactionsById(Long id) {
        EquipmentTransaction equipmentTransaction = equipmentTransactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Transaction Not Found"));

        EquipmentTransactionDTO equipmentTransactionDTO = modelMapper.map(equipmentTransaction, EquipmentTransactionDTO.class);

        equipmentTransactionDTO.getUser().setEquipmentTransactions(null);
        equipmentTransactionDTO.getUser().setSupplyTransactions(null);

        return Response.builder()
                .status(200)
                .message("success")
                .equipmentTransaction(equipmentTransactionDTO)
                .build();
    }

    @Override
    public Response getAllEquipmentTransactionsByMonthAndYear(int month, int year) {
        List<EquipmentTransaction> equipmentTransactions = equipmentTransactionRepository.findAll(EquipmentTransactionFilter.byMonthAndYear(month, year));

        List<EquipmentTransactionDTO> equipmentTransactionDTOS = modelMapper.map(equipmentTransactions, new TypeToken<List<EquipmentTransactionDTO>>() {
        }.getType());

        equipmentTransactionDTOS.forEach(equipmentTransactionDTO -> {
            equipmentTransactionDTO.setUser(null);
            equipmentTransactionDTO.setEquipment(null);
        });

        return Response.builder()
                .status(200)
                .message("success")
                .equipmentTransactions(equipmentTransactionDTOS)
                .build();
    }

    @Override
    public Response startMaintenance(EquipmentTransactionRequest equipmentTransactionRequest) {
        return null;
    }

    @Override
    public Response endMaintenance(EquipmentTransactionRequest equipmentTransactionRequest) {
        return null;
    }

}
