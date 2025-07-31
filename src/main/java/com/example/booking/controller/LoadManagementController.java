package com.example.booking.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
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
import com.example.booking.services.LoadService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/loads")
@RequiredArgsConstructor
public class LoadManagementController {
    private final LoadService loadService;

    @GetMapping
    public ResponseEntity<Page<Load>> getLoads(
            @RequestParam(required = false) String shipperId,
            @RequestParam(required = false) String truckType,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(loadService.getLoads(shipperId, truckType, status, page, size));
    }

    @GetMapping("/{loadId}")
    public ResponseEntity<Load> getLoad(@PathVariable UUID loadId) {
        return ResponseEntity.ok(loadService.getLoadById(loadId));
    }

    @PostMapping
    public ResponseEntity<Load> createLoad(@RequestBody CreateLoadDTO load) {
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
    public ResponseEntity<Load> updateLoad(@PathVariable UUID loadId, @RequestBody Load updatedLoad) {
        return ResponseEntity.ok(loadService.updateLoad(loadId, updatedLoad));
    }

    @DeleteMapping("/{loadId}")
    public ResponseEntity<Void> deleteLoad(@PathVariable UUID loadId) {
        loadService.deleteLoad(loadId);
        return ResponseEntity.noContent().build();
    }

}
