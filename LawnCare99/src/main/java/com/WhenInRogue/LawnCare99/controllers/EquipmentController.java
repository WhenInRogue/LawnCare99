package com.WhenInRogue.LawnCare99.controllers;

import com.WhenInRogue.LawnCare99.dtos.EquipmentDTO;
import com.WhenInRogue.LawnCare99.dtos.Response;
import com.WhenInRogue.LawnCare99.enums.EquipmentStatus;
import com.WhenInRogue.LawnCare99.services.EquipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/equipment")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> createEquipment(@RequestBody @Valid EquipmentDTO equipmentDTO) {
        return ResponseEntity.ok(equipmentService.createEquipment(equipmentDTO));
    }

    @GetMapping("/all")
    public ResponseEntity<Response> getAllEquipment(@RequestParam(value = "status", required = false) EquipmentStatus status) {
        return ResponseEntity.ok(equipmentService.getAllEquipment(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getEquipmentById(@PathVariable Long id) {
        return ResponseEntity.ok(equipmentService.getEquipmentById(id));
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> updateEquipment(@PathVariable Long id, @RequestBody @Valid EquipmentDTO equipmentDTO) {
        return ResponseEntity.ok(equipmentService.updateEquipment(id, equipmentDTO));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> deleteEquipment(@PathVariable Long id) {
        return ResponseEntity.ok(equipmentService.deleteEquipment(id));
    }
}
