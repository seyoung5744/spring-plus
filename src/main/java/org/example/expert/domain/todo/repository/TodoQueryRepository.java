package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.dto.SearchTodoCond;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.dto.TodoSummaryProjection;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface TodoQueryRepository {

    Optional<Todo> findByIdWithUser(Long todoId);

    Page<TodoSummaryProjection> searchTodos(SearchTodoCond cond);
}
