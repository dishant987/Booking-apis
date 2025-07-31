package com.example.booking.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "loads")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Load {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID id;

    private String shipperId;

    private String loadingPoint;
    private String unloadingPoint;
    private Instant loadingDate;
    private Instant unloadingDate;

    private String productType;
    private String truckType;

    private int noOfTrucks;
    private double weight;
    private String comment;

    private Instant datePosted;

    @Enumerated(EnumType.STRING)
    private LoadStatus status = LoadStatus.POSTED;
    @Version
    private Long version;

    public enum LoadStatus {
        POSTED,
        BOOKED,
        CANCELLED
    }
}
