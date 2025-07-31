package com.example.booking.controller;

import java.util.List;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.booking.entity.Booking;
import com.example.booking.entity.Load;
import com.example.booking.entity.Booking.BookingStatus;
import com.example.booking.model.CreateBookingDTO;
import com.example.booking.repository.LoadRepository;
import com.example.booking.services.BookingService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookingManagementController {
    private final BookingService bookingService;
    private final LoadRepository loadRepository;

    @PostMapping
    public ResponseEntity<?> createBooking(
            @RequestParam UUID loadId,
            @RequestBody CreateBookingDTO booking) {
        if (loadId == null || booking == null) {
            return ResponseEntity.badRequest().body("Load ID or booking data is missing.");
        }

        Load load = loadRepository.findById(loadId).orElse(null);
        if (load == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Load not found.");
        }

        if (load.getStatus() == Load.LoadStatus.CANCELLED) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Booking cannot be created for a cancelled load.");
        }
        Booking newBooking = new Booking();

        newBooking.setTransporterId(booking.getTransporterId());
        newBooking.setProposedRate(booking.getProposedRate());
        newBooking.setComment(booking.getComment());
        Booking savedBooking = bookingService.createBooking(load, newBooking);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBooking);
    }

    @GetMapping
    public ResponseEntity<?> getBookings(
            @RequestParam(required = false) UUID loadId,
            @RequestParam(required = false) String transporterId,
            @RequestParam(required = false) BookingStatus status,
            @PageableDefault(size = 10, sort = "requestedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Booking> bookings = bookingService.getBookings(loadId, transporterId, status, pageable);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<?> getBookingById(@PathVariable UUID bookingId) {
        Booking booking = bookingService.getBookingById(bookingId);
        if (booking == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Booking with ID " + bookingId + " not found.");
        }
        return ResponseEntity.ok(booking);
    }

    @PutMapping("/{bookingId}")
    public ResponseEntity<?> updateBooking(@PathVariable UUID bookingId, @RequestBody Booking booking) {
        try {
            Booking updatedBooking = bookingService.updateBooking(bookingId, booking);
            return ResponseEntity.ok(updatedBooking);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Cannot update: Booking with ID " + bookingId + " not found.");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("Invalid booking data: " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error occurred while updating booking.");
        }
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<?> deleteBooking(@PathVariable UUID bookingId) {
        try {
            bookingService.deleteBooking(bookingId);
            return ResponseEntity.ok("Booking with ID " + bookingId + " has been deleted successfully.");
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Booking with ID " + bookingId + " not found.");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error occurred while deleting booking.");
        }
    }
}
