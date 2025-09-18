package org.example.expert.domain.todo.dto.request;

import org.example.expert.domain.common.annotation.DateRange;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@DateRange
public record SearchTodoRequest(
        Integer page,
        Integer size,
        String title,
        String nickname,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
) {
}
