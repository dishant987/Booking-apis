package com.example.booking.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.booking.entity.Booking;
import com.example.booking.entity.Booking.BookingStatus;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    Page<Booking> findByLoad_IdAndTransporterIdAndStatus(UUID loadId, String transporterId, BookingStatus status,
            Pageable pageable);

    Page<Booking> findByLoad_Id(UUID loadId, Pageable pageable);

    Page<Booking> findByTransporterId(String transporterId, Pageable pageable);

    Page<Booking> findByStatus(BookingStatus status, Pageable pageable);
}
