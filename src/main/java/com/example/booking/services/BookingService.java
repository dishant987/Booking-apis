package com.example.booking.services;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.booking.entity.Booking;
import com.example.booking.entity.Load;
import com.example.booking.repository.BookingRepository;
import com.example.booking.repository.LoadRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;

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

    public Booking updateBooking(UUID id, Booking updatedBooking) {
        Booking existing = getBookingById(id);
        existing.setTransporterId(updatedBooking.getTransporterId());
        existing.setProposedRate(updatedBooking.getProposedRate());
        existing.setComment(updatedBooking.getComment());
        existing.setStatus(updatedBooking.getStatus());
        return bookingRepository.save(existing);
    }

    public void deleteBooking(UUID id) {
        Booking booking = getBookingById(id);
        bookingRepository.delete(booking);
    }
}
