package com.example.booking.controller;

import java.util.EnumSet;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.booking.entity.Booking;
import com.example.booking.entity.Load;
import com.example.booking.entity.Booking.BookingStatus;
import com.example.booking.model.BookingResponseDTO;
import com.example.booking.model.CreateBookingDTO;
import com.example.booking.model.LoadResponseDTO;
import com.example.booking.model.UpdateBookingDTO;
import com.example.booking.model.UpdateLoadDTO;
import com.example.booking.repository.LoadRepository;
import com.example.booking.services.BookingService;
import com.example.booking.services.LoadService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookingManagementController {
    private final BookingService bookingService;
    private final LoadService loadService;
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
        load.setStatus(Load.LoadStatus.BOOKED);
        newBooking.setTransporterId(booking.getTransporterId());
        newBooking.setProposedRate(booking.getProposedRate());
        newBooking.setComment(booking.getComment());
        newBooking.setStatus(Booking.BookingStatus.ACCEPTED);
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
        BookingResponseDTO response = new BookingResponseDTO(
                bookings.getContent(),
                bookings.getSize(),
                bookings.getNumber() + 1,
                bookings.getTotalElements(),
                bookings.getTotalPages(),
                bookings.isLast());
        return ResponseEntity.ok(response);
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
    public ResponseEntity<?> updateBooking(@PathVariable UUID bookingId, @RequestBody UpdateBookingDTO bookingDto) {
        try {
            // Optional: Validate if DTO has mismatching ID (if ID exists in DTO)
            if (bookingDto.getId() != null && !bookingDto.getId().equals(bookingId)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Booking ID in the path and body do not match."));
            }

            // Optional: Validate status if it's not part of the enum
            if (bookingDto.getStatus() != null &&
                    !EnumSet.allOf(Booking.BookingStatus.class).contains(bookingDto.getStatus())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid booking status: " + bookingDto.getStatus()));
            }

            // Perform update
            Booking updatedBooking = bookingService.updateBooking(bookingId, bookingDto);

            return ResponseEntity.ok(Map.of(
                    "message", "Booking updated successfully.",
                    "booking", updatedBooking));

        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            ex.printStackTrace(); // Log only during development
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred while updating the booking."));
        }
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<?> deleteBooking(@PathVariable UUID bookingId) {
        try {
            // Get booking
            Booking booking = bookingService.getBookingById(bookingId);
            if (booking == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Booking with ID " + bookingId + " not found.");
            }

            // Get associated load
            UUID loadId = booking.getLoad().getId();
            Load load = loadRepository.findById(loadId).orElse(null);
            if (load == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Associated load with ID " + loadId + " not found.");
            }

            // Prepare update DTO to cancel the load
            UpdateLoadDTO updateLoadDTO = UpdateLoadDTO.builder()
                    .status(UpdateLoadDTO.LoadStatus.CANCELLED)
                    .build();
            loadService.updateLoad(loadId, updateLoadDTO);

            bookingService.deleteBooking(bookingId);

            return ResponseEntity.ok("Booking deleted successfully.");

        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Entity not found: " + ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace(); // log error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error occurred while deleting the booking.");
        }
    }

}
