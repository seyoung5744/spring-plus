package org.example.expert.domain.todo.repository.dto;

import com.querydsl.core.annotations.QueryProjection;

public record TodoSummaryProjection(
        Long id,
        String title,
        Long managerCount,
        Long commentCount
) {
    @QueryProjection
    public TodoSummaryProjection {
    }
}
