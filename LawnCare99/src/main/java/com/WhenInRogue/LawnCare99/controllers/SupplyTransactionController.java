package com.WhenInRogue.LawnCare99.controllers;

import com.WhenInRogue.LawnCare99.dtos.Response;
import com.WhenInRogue.LawnCare99.dtos.SupplyTransactionRequest;
import com.WhenInRogue.LawnCare99.services.SupplyTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/supplyTransactions")
@RequiredArgsConstructor
public class SupplyTransactionController {

    private final SupplyTransactionService supplyTransactionService;

    @PostMapping("/checkInSupply")
    public ResponseEntity<Response> checkInSupply(@RequestBody @Valid SupplyTransactionRequest supplyTransactionRequest) {
        return ResponseEntity.ok(supplyTransactionService.checkInSupply(supplyTransactionRequest));
    }

    @PostMapping("/checkOutSupply")
    public ResponseEntity<Response> checkOutSupply(@RequestBody @Valid SupplyTransactionRequest supplyTransactionRequest) {
        return ResponseEntity.ok(supplyTransactionService.checkOutSupply(supplyTransactionRequest));
    }

    @GetMapping("/all")
    public ResponseEntity<Response> getAllSupplyTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1000") int size,
            @RequestParam(required = false) String filter) {

        System.out.println("SEARCH VALUE IS: " +filter);
        return ResponseEntity.ok(supplyTransactionService.getAllSupplyTransactions(page, size, filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getSupplyTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(supplyTransactionService.getAllSupplyTransactionsById(id));
    }

    @GetMapping("/by-month-year")
    public ResponseEntity<Response> getSupplyTransactionsByMonthAndYear(
            @RequestParam int month,
            @RequestParam int year) {

        return ResponseEntity.ok(supplyTransactionService.getAllSupplyTransactionsByMonthAndYear(month, year));
    }
}
