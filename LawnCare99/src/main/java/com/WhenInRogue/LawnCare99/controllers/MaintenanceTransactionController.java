package com.WhenInRogue.LawnCare99.controllers;

import com.WhenInRogue.LawnCare99.dtos.MaintenanceTransactionRequest;
import com.WhenInRogue.LawnCare99.dtos.Response;
import com.WhenInRogue.LawnCare99.services.MaintenanceTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/maintenanceTransactions")
@RequiredArgsConstructor
public class MaintenanceTransactionController {

    private final MaintenanceTransactionService maintenanceTransactionService;

    @PostMapping("/start")
    public ResponseEntity<Response> startMaintenance(@RequestBody @Valid MaintenanceTransactionRequest maintenanceTransactionRequest) {
        return ResponseEntity.ok(maintenanceTransactionService.startMaintenance(maintenanceTransactionRequest));
    }

    @PostMapping("/complete")
    public ResponseEntity<Response> completeMaintenance(@RequestBody @Valid MaintenanceTransactionRequest maintenanceTransactionRequest) {
        return ResponseEntity.ok(maintenanceTransactionService.completeMaintenance(maintenanceTransactionRequest));
    }

    @GetMapping("/equipment/{equipmentId}")
    public ResponseEntity<Response> getMaintenanceTransactionsByEquipment(@PathVariable Long equipmentId) {
        return ResponseEntity.ok(maintenanceTransactionService.getMaintenanceTransactionsByEquipment(equipmentId));
    }
}
