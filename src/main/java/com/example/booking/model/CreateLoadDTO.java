package com.example.booking.model;

import lombok.*;

import java.time.Instant;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateLoadDTO {

    @NotBlank(message = "Shipper ID is required")
    private String shipperId;

    @NotBlank(message = "Loading point is required")
    private String loadingPoint;

    @NotBlank(message = "Unloading point is required")
    private String unloadingPoint;

    @NotNull(message = "Loading date is required")
    private Instant loadingDate;

    @NotNull(message = "Unloading date is required")
    private Instant unloadingDate;

    @NotBlank(message = "Product type is required")
    private String productType;

    @NotBlank(message = "Truck type is required")
    private String truckType;

    @Min(value = 1, message = "At least one truck is required")
    private int noOfTrucks;

    @DecimalMin(value = "0.1", message = "Weight must be greater than 0")
    private double weight;

    // Optional field
    private String comment;
}
