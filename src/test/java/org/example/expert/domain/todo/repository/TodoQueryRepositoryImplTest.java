package org.example.expert.domain.todo.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.example.expert.config.QuerydslConfig;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.todo.dto.SearchTodoCond;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.dto.TodoSummaryProjection;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@DataJpaTest
@Import(QuerydslConfig.class)
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class TodoQueryRepositoryImplTest {

    @Autowired
    TestEntityManager em;

    @Autowired
    JPAQueryFactory factory;

    TodoQueryRepository todoQueryRepository;
    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @BeforeEach
    void setUp() {
        todoQueryRepository = new TodoQueryRepositoryImpl(factory);

        User user1 = new User("test1@test.com", "password", "aa", UserRole.USER);
        em.persist(user1);
        User user2 = new User("test2@test.com", "password", "aaa", UserRole.USER);
        em.persist(user2);
        User user3 = new User("test3@test.com", "password", "bb", UserRole.USER);
        em.persist(user3);
        User user4 = new User("test4@test.com", "password", "abab", UserRole.USER);
        em.persist(user4);

        Todo todo1 = new Todo("todo1", "내용", "날씨", user1);
        ReflectionTestUtils.setField(todo1, "createdAt", LocalDate.of(2025, 9, 15).atStartOfDay());
        em.persist(todo1);

        Todo todo2 = new Todo("todo2", "내용", "날씨", user2);
        ReflectionTestUtils.setField(todo2, "createdAt", LocalDate.of(2025, 9, 18).atStartOfDay());
        em.persist(todo2);

        Manager todo1Manager1 = new Manager(user2, todo1);
        em.persist(todo1Manager1);
        Manager todo1Manager2 = new Manager(user3, todo1);
        em.persist(todo1Manager2);
        Manager todo1Manager3 = new Manager(user4, todo1);
        em.persist(todo1Manager3);

        Manager todo2Manager1 = new Manager(user4, todo2);
        em.persist(todo2Manager1);

        Comment comment1 = new Comment("댓글 내용1", user1, todo1);
        em.persist(comment1);
        Comment comment2 = new Comment("댓글 내용1", user1, todo1);
        em.persist(comment2);
        Comment comment3 = new Comment("댓글 내용1", user2, todo1);
        em.persist(comment3);

        em.flush();
    }

    @Test
    void TODO_제목이_일치할_경우() {
        // given
        SearchTodoCond searchTodoCond = SearchTodoCond.builder()
                .pageable(PageRequest.of(0, 10))
                .startDateTime(LocalDate.of(2025, 9, 15).atStartOfDay())
                .endDateTime(LocalDate.of(2025, 9, 18).atStartOfDay().plusDays(1))
                .title("todo1")
                .build();

        // when
        Page<TodoSummaryProjection> result = todoQueryRepository.searchTodos(searchTodoCond);

        // then
        assertThat(result.getContent()).hasSize(1)
                .extracting("title", "managerCount")
                .containsOnly(tuple("todo1", 4L));
    }

    @Test
    void TODO_담장자_닉네임이_일치할_경우() {
        // given
        SearchTodoCond searchTodoCond = SearchTodoCond.builder()
                .pageable(PageRequest.of(0, 10))
                .startDateTime(LocalDate.of(2025, 9, 15).atStartOfDay())
                .endDateTime(LocalDate.of(2025, 9, 18).atStartOfDay().plusDays(1))
                .nickname("a")
                .build();

        // when
        Page<TodoSummaryProjection> result = todoQueryRepository.searchTodos(searchTodoCond);
        System.out.println(result.getContent());

        // then
        assertThat(result.getContent()).hasSize(2)
                .extracting("title", "managerCount")
                .contains(
                        tuple("todo1", 4L),
                        tuple("todo2", 2L)
                );
    }
}