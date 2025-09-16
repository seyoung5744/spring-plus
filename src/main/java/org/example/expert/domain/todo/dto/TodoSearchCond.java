package org.example.expert.domain.todo.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record TodoSearchCond(
        String weather,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
) {
}
