package com.example.booking.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingResponseDTO {
    private List<BookingDTO> content;
    private int pageSize;
    private int pageNumber;
    private long totalCount;
    private int totalPages;
    private boolean last;
}
