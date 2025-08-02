package com.example.booking.model;

import java.util.List;

import com.example.booking.entity.Load;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoadResponseDTO {
    private List<Load> content;
    private int pageSize;
    private int pageNumber;
    private long totalCount;
    private int totalPages;
    private boolean last;
}
