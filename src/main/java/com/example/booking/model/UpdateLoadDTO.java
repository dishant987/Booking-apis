package com.example.booking.model;

import java.time.Instant;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateLoadDTO {

    @NotBlank(message = "Shipper ID is required")
    private String shipperId;

    private String loadingPoint;
    private String unloadingPoint;

    private Instant loadingDate;
    private Instant unloadingDate;

    private String productType;
    private String truckType;

    @Min(value = 1, message = "At least one truck is required")
    private int noOfTrucks;

    @DecimalMin(value = "0.1", message = "Weight must be greater than 0")
    private double weight;

    private String comment;

    private LoadStatus status;

    public enum LoadStatus {
        POSTED,
        BOOKED,
        CANCELLED
    }
}
