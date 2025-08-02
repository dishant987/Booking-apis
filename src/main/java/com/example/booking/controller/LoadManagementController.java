package com.example.booking.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.booking.entity.Load;
import com.example.booking.model.CreateLoadDTO;
import com.example.booking.model.LoadResponseDTO;
import com.example.booking.model.UpdateLoadDTO;
import com.example.booking.services.LoadService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/loads")
@RequiredArgsConstructor
public class LoadManagementController {
    private final LoadService loadService;

    @GetMapping
    public ResponseEntity<?> getLoads(
            @RequestParam(required = false) String shipperId,
            @RequestParam(required = false) String truckType,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 10, sort = "datePosted", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            // Validate truckType (if needed - add business rule)
            if (truckType != null && truckType.isBlank()) {
                return ResponseEntity.badRequest().body("truckType must not be blank.");
            }

            // Validate shipperId (if needed - add business rule)
            if (shipperId != null && shipperId.isBlank()) {
                return ResponseEntity.badRequest().body("shipperId must not be blank.");
            }

            // Validate status
            Load.LoadStatus validatedStatus = null;
            if (status != null && !status.isBlank()) {
                try {
                    validatedStatus = Load.LoadStatus.valueOf(status.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest()
                            .body("Invalid status. Allowed values: POSTED, BOOKED, CANCELLED.");
                }
            }

            // Forward the request
            Page<Load> loads = loadService.getLoads(shipperId, truckType,
                    validatedStatus != null ? validatedStatus.name() : null, pageable);
            LoadResponseDTO response = new LoadResponseDTO(
                    loads.getContent(),
                    loads.getSize(),
                    loads.getNumber() + 1,
                    loads.getTotalElements(),
                    loads.getTotalPages(),
                    loads.isLast());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/{loadId}")
    public ResponseEntity<Load> getLoad(@PathVariable UUID loadId) {
        return ResponseEntity.ok(loadService.getLoadById(loadId));
    }

    @PostMapping
    public ResponseEntity<?> createLoad(@RequestBody CreateLoadDTO load) {
        boolean exists = loadService.existsByShipperId(load.getShipperId());
        if (exists) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("A load with the same shipperId already exists.");
        }
        Load newLoad = new Load();
        newLoad.setShipperId(load.getShipperId());
        newLoad.setLoadingPoint(load.getLoadingPoint());
        newLoad.setUnloadingPoint(load.getUnloadingPoint());
        newLoad.setLoadingDate(load.getLoadingDate());
        newLoad.setUnloadingDate(load.getUnloadingDate());
        newLoad.setProductType(load.getProductType());
        newLoad.setTruckType(load.getTruckType());
        newLoad.setNoOfTrucks(load.getNoOfTrucks());
        newLoad.setWeight(load.getWeight());
        newLoad.setComment(load.getComment());
        return ResponseEntity.ok(loadService.createLoad(newLoad));
    }

    @PutMapping("/{loadId}")
    public ResponseEntity<?> updateLoad(
            @PathVariable UUID loadId,
            @Valid @RequestBody UpdateLoadDTO updatedLoadDTO) {
        try {
            Load updatedLoad = loadService.updateLoad(loadId, updatedLoadDTO);
            return ResponseEntity.ok(updatedLoad);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update load: " + ex.getMessage()));
        }
    }

    @DeleteMapping("/{loadId}")
    public ResponseEntity<Void> deleteLoad(@PathVariable UUID loadId) {
        loadService.deleteLoad(loadId);
        return ResponseEntity.noContent().build();
    }

}
