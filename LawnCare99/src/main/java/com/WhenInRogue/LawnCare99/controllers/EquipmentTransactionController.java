package com.WhenInRogue.LawnCare99.controllers;

import com.WhenInRogue.LawnCare99.dtos.EquipmentTransactionRequest;
import com.WhenInRogue.LawnCare99.dtos.Response;
import com.WhenInRogue.LawnCare99.services.EquipmentTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/equipmentTransactions")
@RequiredArgsConstructor
public class EquipmentTransactionController {

    private final EquipmentTransactionService equipmentTransactionService;

    @PostMapping("/checkInEquipment")
    public ResponseEntity<Response> checkInEquipment(@RequestBody @Valid EquipmentTransactionRequest equipmentTransactionRequest) {
        return ResponseEntity.ok(equipmentTransactionService.checkInEquipment(equipmentTransactionRequest));
    }

    @PostMapping("/checkOutEquipment")
    public ResponseEntity<Response> checkOutSupply(@RequestBody @Valid EquipmentTransactionRequest equipmentTransactionRequest) {
        return ResponseEntity.ok(equipmentTransactionService.checkOutEquipment(equipmentTransactionRequest));
    }

    @GetMapping("/all")
    public ResponseEntity<Response> getAllEquipmentTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1000") int size,
            @RequestParam(required = false) String filter) {

        System.out.println("SEARCH VALUE IS: " +filter);
        return ResponseEntity.ok(equipmentTransactionService.getAllEquipmentTransactions(page, size, filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getEquipmentTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(equipmentTransactionService.getAllEquipmentTransactionsById(id));
    }

    @GetMapping("/by-month-year")
    public ResponseEntity<Response> getEquipmentTransactionsByMonthAndYear(
            @RequestParam int month,
            @RequestParam int year) {

        return ResponseEntity.ok(equipmentTransactionService.getAllEquipmentTransactionsByMonthAndYear(month, year));
    }

}
