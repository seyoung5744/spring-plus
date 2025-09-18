package org.example.expert.domain.todo.dto.response;

import lombok.Builder;
import org.example.expert.domain.todo.repository.dto.TodoSummaryProjection;

@Builder
public record SearchTodoResponse(
        String title,
        Long managerCount,
        Long commentCount
) {

    public static SearchTodoResponse of(TodoSummaryProjection projection) {
        return SearchTodoResponse.builder()
                .title(projection.title())
                .managerCount(projection.managerCount())
                .commentCount(projection.commentCount())
                .build();
    }
}
