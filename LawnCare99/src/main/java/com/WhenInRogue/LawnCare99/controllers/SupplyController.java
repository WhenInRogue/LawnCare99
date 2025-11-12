package com.WhenInRogue.LawnCare99.controllers;

import com.WhenInRogue.LawnCare99.dtos.Response;
import com.WhenInRogue.LawnCare99.dtos.SupplyDTO;
import com.WhenInRogue.LawnCare99.services.SupplyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/supplies")
@RequiredArgsConstructor
public class SupplyController {

    private final SupplyService supplyService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> createSupply(@RequestBody @Valid SupplyDTO supplyDTO) {
        return ResponseEntity.ok(supplyService.createSupply(supplyDTO));
    }

    @GetMapping("/all")
    public ResponseEntity<Response> getAllSupplies() {
        return ResponseEntity.ok(supplyService.getAllSupplies());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getSupplyById(@PathVariable Long id) {
        return ResponseEntity.ok(supplyService.getSupplyById(id));
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> updateSupply(@PathVariable Long id, @RequestBody @Valid SupplyDTO supplyDTO) {
        return ResponseEntity.ok(supplyService.updateSupply(id, supplyDTO));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> deleteSupply(@PathVariable Long id) {
        return ResponseEntity.ok(supplyService.deleteSupply(id));
    }
}
