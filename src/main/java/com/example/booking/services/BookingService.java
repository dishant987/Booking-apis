package com.example.booking.services;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.booking.entity.Booking;
import com.example.booking.entity.Load;
import com.example.booking.model.UpdateBookingDTO;
import com.example.booking.repository.BookingRepository;
import com.example.booking.repository.LoadRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final LoadRepository loadRepository;

    public Booking createBooking(Load load, Booking booking) {
        booking.setLoad(load);
        booking.setRequestedAt(Instant.now());
        return bookingRepository.save(booking);
    }

    public Page<Booking> getBookings(UUID loadId, String transporterId, Booking.BookingStatus status,
            Pageable pageable) {
        if (loadId != null && transporterId != null && status != null) {
            return bookingRepository.findByLoad_IdAndTransporterIdAndStatus(loadId, transporterId, status, pageable);
        } else if (loadId != null) {
            return bookingRepository.findByLoad_Id(loadId, pageable);
        } else if (transporterId != null) {
            return bookingRepository.findByTransporterId(transporterId, pageable);
        } else if (status != null) {
            return bookingRepository.findByStatus(status, pageable);
        } else {
            return bookingRepository.findAll(pageable);
        }
    }

    public Booking getBookingById(UUID id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with ID: " + id));
    }

    public Booking updateBooking(UUID id, UpdateBookingDTO updatedBooking) {
        Booking existing = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking with ID " + id + " not found."));

        // Only update fields that are non-null and valid
        if (updatedBooking.getTransporterId() != null) {
            existing.setTransporterId(updatedBooking.getTransporterId());
        }

        if (updatedBooking.getProposedRate() != null) {
            if (updatedBooking.getProposedRate() <= 0) {
                throw new IllegalArgumentException("Proposed rate must be greater than 0.");
            }
            existing.setProposedRate(updatedBooking.getProposedRate());
        }

        if (updatedBooking.getComment() != null) {
            existing.setComment(updatedBooking.getComment());
        }

        if (updatedBooking.getStatus() != null) {
            existing.setStatus(updatedBooking.getStatus());
        }

        if (updatedBooking.getLoadId() != null) {
            Load load = loadRepository.findById(updatedBooking.getLoadId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Load with ID " + updatedBooking.getLoadId() + " not found."));
            existing.setLoad(load);
        }

        return bookingRepository.save(existing);
    }

    public void deleteBooking(UUID id) {
        Booking booking = getBookingById(id);
        bookingRepository.delete(booking);
    }
}
