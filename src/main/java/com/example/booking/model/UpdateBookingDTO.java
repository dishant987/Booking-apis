package com.example.booking.model;

import com.example.booking.entity.Booking.BookingStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateBookingDTO {

    @NotNull(message = "Booking ID is required")
    private UUID id;

    private UUID loadId;

    private String transporterId;

    private Double proposedRate; // Use wrapper class to allow null (i.e., field not set)

    private String comment;

    private BookingStatus status;
}
