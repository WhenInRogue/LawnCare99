package com.WhenInRogue.LawnCare99.dtos;

import com.WhenInRogue.LawnCare99.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {

    //Generic
    private int status;
    private String message;
    //for login
    private String token;
    private UserRole role;
    private String expirationTime;

    //for pagination
    private Integer totalPages;
    private Long totalElements;

    //data output optionals
    private UserDTO user;
    private List<UserDTO> users;

    private SupplyDTO supply;
    private List<SupplyDTO> supplies;

    private SupplyTransactionDTO supplyTransaction;
    private List<SupplyTransactionDTO> supplyTransactions;

    private EquipmentDTO equipment;
    private List<EquipmentDTO> equipments;

    private EquipmentTransactionDTO equipmentTransaction;
    private List<EquipmentTransactionDTO> equipmentTransactions;

    private MaintenanceRecordDTO maintenanceRecord;
    private List<MaintenanceRecordDTO> maintenanceRecords;

    private final LocalDateTime timestamp = LocalDateTime.now();


}
