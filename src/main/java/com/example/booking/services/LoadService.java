package com.example.booking.services;

import com.example.booking.entity.Load;
import com.example.booking.model.UpdateLoadDTO;
import com.example.booking.repository.LoadRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoadService {

    private final LoadRepository loadRepository;

    public Page<Load> getLoads(String shipperId, String truckType, String status, Pageable pageable) {
        return loadRepository.findAll(filterBy(shipperId, truckType, status), pageable);
    }

    public Load getLoadById(UUID id) {
        return loadRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Load not found with ID: " + id));
    }

    public boolean existsByShipperId(String shipperId) {
        return loadRepository.existsByShipperId(shipperId);
    }

    public Load createLoad(Load load) {
        load.setDatePosted(Instant.now());
        load.setStatus(Load.LoadStatus.POSTED); // default status
        return loadRepository.save(load);
    }

    public Load updateLoad(UUID id, UpdateLoadDTO updatedLoadDTO) {
        Load existingLoad = loadRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Load with ID " + id + " not found."));

        // Only update fields if they are not null in DTO (optional update)
        if (updatedLoadDTO.getShipperId() != null)
            existingLoad.setShipperId(updatedLoadDTO.getShipperId());

        if (updatedLoadDTO.getLoadingPoint() != null)
            existingLoad.setLoadingPoint(updatedLoadDTO.getLoadingPoint());

        if (updatedLoadDTO.getUnloadingPoint() != null)
            existingLoad.setUnloadingPoint(updatedLoadDTO.getUnloadingPoint());

        if (updatedLoadDTO.getLoadingDate() != null)
            existingLoad.setLoadingDate(updatedLoadDTO.getLoadingDate());

        if (updatedLoadDTO.getUnloadingDate() != null)
            existingLoad.setUnloadingDate(updatedLoadDTO.getUnloadingDate());

        if (updatedLoadDTO.getProductType() != null)
            existingLoad.setProductType(updatedLoadDTO.getProductType());

        if (updatedLoadDTO.getTruckType() != null)
            existingLoad.setTruckType(updatedLoadDTO.getTruckType());

        if (updatedLoadDTO.getNoOfTrucks() > 0)
            existingLoad.setNoOfTrucks(updatedLoadDTO.getNoOfTrucks());

        if (updatedLoadDTO.getWeight() > 0)
            existingLoad.setWeight(updatedLoadDTO.getWeight());

        if (updatedLoadDTO.getComment() != null)
            existingLoad.setComment(updatedLoadDTO.getComment());

        if (updatedLoadDTO.getStatus() != null)
            existingLoad.setStatus(Load.LoadStatus.valueOf(updatedLoadDTO.getStatus().name()));

        return loadRepository.save(existingLoad);
    }

    public void deleteLoad(UUID id) {
        if (!loadRepository.existsById(id)) {
            throw new EntityNotFoundException("Load not found with ID: " + id);
        }
        loadRepository.deleteById(id);
    }

    public static Specification<Load> filterBy(String shipperId, String truckType, String status) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (shipperId != null && !shipperId.isEmpty()) {
                predicate = cb.and(predicate, cb.equal(root.get("shipperId"), shipperId));
            }

            if (truckType != null && !truckType.isEmpty()) {
                predicate = cb.and(predicate, cb.equal(root.get("truckType"), truckType));
            }

            if (status != null && !status.isEmpty()) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), Load.LoadStatus.valueOf(status)));
            }

            return predicate;
        };
    }
}
