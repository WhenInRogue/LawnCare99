package com.WhenInRogue.LawnCare99.controllers;

import com.WhenInRogue.LawnCare99.dtos.MaintenanceRequest;
import com.WhenInRogue.LawnCare99.dtos.Response;
import com.WhenInRogue.LawnCare99.services.MaintenanceRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/maintenance")
@RequiredArgsConstructor
public class MaintenanceRecordController {

    private final MaintenanceRecordService maintenanceRecordService;

    @PostMapping("/start")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> startMaintenance(@RequestBody @Valid MaintenanceRequest maintenanceRequest) {
        return ResponseEntity.ok(maintenanceRecordService.startMaintenance(maintenanceRequest));
    }

    @PostMapping("/end")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> endMaintenance(@RequestBody @Valid MaintenanceRequest maintenanceRequest) {
        return ResponseEntity.ok(maintenanceRecordService.endMaintenance(maintenanceRequest));
    }

    @GetMapping("/records")
    public ResponseEntity<Response> getAllMaintenanceRecords() {
        return ResponseEntity.ok(maintenanceRecordService.getAllMaintenanceRecords());
    }

    @GetMapping("/records/equipment/{equipmentId}")
    public ResponseEntity<Response> getMaintenanceRecordsByEquipment(@PathVariable Long equipmentId) {
        return ResponseEntity.ok(maintenanceRecordService.getMaintenanceRecordsByEquipment(equipmentId));
    }
}
