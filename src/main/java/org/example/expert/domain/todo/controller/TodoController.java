package org.example.expert.domain.todo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.todo.dto.SearchTodoCond;
import org.example.expert.domain.todo.dto.TodoSearchCond;
import org.example.expert.domain.todo.dto.request.SearchTodoRequest;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.SearchTodoResponse;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.service.TodoService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TodoController {

    private static final long END_DATE_OFFSET_DAYS = 1L;

    private final TodoService todoService;

    @PostMapping("/todos")
    public ResponseEntity<TodoSaveResponse> saveTodo(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody TodoSaveRequest todoSaveRequest
    ) {
        return ResponseEntity.ok(todoService.saveTodo(authUser, todoSaveRequest));
    }

    @GetMapping("/todos")
    public ResponseEntity<Page<TodoResponse>> getTodos(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @ModelAttribute TodoSearchCond todoSearchCond
    ) {
        validateDateRange(todoSearchCond.startDate(), todoSearchCond.endDate());

        LocalDateTime startDateTime = (todoSearchCond.startDate() != null) ? todoSearchCond.startDate().atStartOfDay() : null;
        LocalDateTime endDateTime = (todoSearchCond.startDate() != null) ? todoSearchCond.endDate().atStartOfDay().plusDays(END_DATE_OFFSET_DAYS) : null;

        return ResponseEntity.ok(todoService.getTodos(page, size, todoSearchCond.weather(), startDateTime, endDateTime));
    }

    @GetMapping("/todos/{todoId}")
    public ResponseEntity<TodoResponse> getTodo(@PathVariable long todoId) {
        return ResponseEntity.ok(todoService.getTodo(todoId));
    }

    @GetMapping("/todos/search")
    public ResponseEntity<Page<SearchTodoResponse>> searchTodos(@Valid @ModelAttribute SearchTodoRequest request) {
        SearchTodoCond searchTodoCond = SearchTodoCond.from(request);
        return ResponseEntity.ok(todoService.searchTodos(searchTodoCond));
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("endDate must not be before startDate");
        }
    }
}
