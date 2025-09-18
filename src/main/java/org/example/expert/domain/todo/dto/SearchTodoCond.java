package org.example.expert.domain.todo.dto;

import lombok.Builder;
import org.example.expert.domain.todo.dto.request.SearchTodoRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

@Builder
public record SearchTodoCond(
        Pageable pageable,
        String title,
        String nickname,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
) {

    private static final long END_DATE_OFFSET_DAYS = 1L;

    public static SearchTodoCond from(SearchTodoRequest request) {
        Pageable pageable = toPageable(request.page(), request.size());
        LocalDateTime startDateTime = (request.startDate() != null) ? request.startDate().atStartOfDay() : null;
        LocalDateTime endDateTime = (request.endDate() != null) ? request.endDate().atStartOfDay().plusDays(END_DATE_OFFSET_DAYS) : null;

        return SearchTodoCond.builder()
                .pageable(pageable)
                .title(request.title())
                .nickname(request.nickname())
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .build();
    }

    private static Pageable toPageable(Integer page, Integer size) {
        if (page == null || page < 0) {
            page = 0;
        }

        if (size == null || size < 0) {
            size = 10;
        }

        return PageRequest.of(page, size);
    }
}
