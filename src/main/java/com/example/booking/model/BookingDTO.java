package com.example.booking.model;

import java.time.Instant;
import java.util.UUID;

import com.example.booking.entity.Booking.BookingStatus;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDTO {
    private UUID id;
    private String comment;
    private UUID loadId;
    private String loadComment;
    private String transporterId;
    private double proposedRate;
    private Instant requestedAt;
    private BookingStatus status;
}
