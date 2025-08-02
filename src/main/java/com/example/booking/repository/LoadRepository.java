package com.example.booking.repository;

import com.example.booking.entity.Load;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LoadRepository extends JpaRepository<Load, UUID>, JpaSpecificationExecutor<Load> {
    boolean existsByShipperId(String shipperId);
}
