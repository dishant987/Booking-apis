package com.example.booking.services;

import com.example.booking.entity.Load;
import com.example.booking.repository.LoadRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoadService {

    private final LoadRepository loadRepository;

    public Page<Load> getLoads(String shipperId, String truckType, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return loadRepository.findAll(filterBy(shipperId, truckType, status), pageable);
    }

    public Load getLoadById(UUID id) {
        return loadRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Load not found with ID: " + id));
    }

    public Load createLoad(Load load) {
        load.setDatePosted(Instant.now());
        load.setStatus(Load.LoadStatus.POSTED); // default status
        return loadRepository.save(load);
    }

    public Load updateLoad(UUID id, Load updatedLoad) {
        Load existingLoad = getLoadById(id);

        existingLoad.setShipperId(updatedLoad.getShipperId());
        existingLoad.setLoadingPoint(updatedLoad.getLoadingPoint());
        existingLoad.setUnloadingPoint(updatedLoad.getUnloadingPoint());
        existingLoad.setLoadingDate(updatedLoad.getLoadingDate());
        existingLoad.setUnloadingDate(updatedLoad.getUnloadingDate());
        existingLoad.setProductType(updatedLoad.getProductType());
        existingLoad.setTruckType(updatedLoad.getTruckType());
        existingLoad.setNoOfTrucks(updatedLoad.getNoOfTrucks());
        existingLoad.setWeight(updatedLoad.getWeight());
        existingLoad.setComment(updatedLoad.getComment());
        existingLoad.setDatePosted(updatedLoad.getDatePosted());
        existingLoad.setStatus(updatedLoad.getStatus());

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
