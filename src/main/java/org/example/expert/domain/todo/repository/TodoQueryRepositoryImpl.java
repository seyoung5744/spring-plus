package org.example.expert.domain.todo.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.comment.entity.QComment;
import org.example.expert.domain.manager.entity.QManager;
import org.example.expert.domain.todo.dto.SearchTodoCond;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.dto.QTodoSummaryProjection;
import org.example.expert.domain.todo.repository.dto.TodoSummaryProjection;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class TodoQueryRepositoryImpl implements TodoQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * query
     * SELECT t FROM Todo t LEFT JOIN t.user WHERE t.id = :todoId
     */
    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
        QTodo todo = QTodo.todo;
        QUser user = QUser.user;

        return Optional.ofNullable(
                jpaQueryFactory
                        .select(todo)
                        .from(todo)
                        .leftJoin(todo.user, user).fetchJoin()
                        .where(todo.id.eq(todoId))
                        .fetchOne()
        );
    }

    @Override
    public Page<TodoSummaryProjection> searchTodos(SearchTodoCond cond) {
        QTodo todo = QTodo.todo;
        QComment comment = QComment.comment;
        QManager manager = QManager.manager;

        // title or nickn
        List<Long> todoIds = jpaQueryFactory.selectDistinct(manager.todo.id)
                .from(manager)
                .where(
                        titleLike(todo, cond.title()),
                        managerNicknameLike(manager, cond.nickname())
                ).fetch();
        List<TodoSummaryProjection> content = jpaQueryFactory.select(
                        new QTodoSummaryProjection(
                                todo.id,
                                todo.title,
                                JPAExpressions.select(manager.count())
                                        .from(manager)
                                        .where(manager.todo.eq(todo)),
                                JPAExpressions.select(comment.count())
                                        .from(comment)
                                        .where(comment.todo.eq(todo))
                        )
                )
                .from(todo)
                .where(
                        titleLike(todo, cond.title()),
                        betweenDate(todo, cond.startDateTime(), cond.endDateTime()),
                        todo.id.in(todoIds)
                )
                .orderBy(todo.createdAt.desc())
                .offset(cond.pageable().getOffset())
                .limit(cond.pageable().getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory.select(todo.count())
                .from(todo)
                .where(
                        titleLike(todo, cond.title()),
                        betweenDate(todo, cond.startDateTime(), cond.endDateTime()),
                        todo.id.in(todoIds)
                );

        return PageableExecutionUtils.getPage(content, cond.pageable(), countQuery::fetchOne);
    }

    private BooleanExpression titleLike(QTodo todo, String title) {
        return StringUtils.hasText(title) ? todo.title.like("%" + title + "%") : null;
    }

    private BooleanBuilder betweenDate(QTodo todo, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        BooleanBuilder builder = new BooleanBuilder();

        if (startDateTime != null) {
            builder.and(todo.createdAt.goe(startDateTime));
        }

        if (endDateTime != null) {
            builder.and(todo.createdAt.lt(endDateTime));
        }

        return builder;
    }

    private BooleanExpression managerNicknameLike(QManager manager, String nickname) {
        return StringUtils.hasText(nickname) ? manager.user.nickname.like("%" + nickname + "%") : null;
    }

}
