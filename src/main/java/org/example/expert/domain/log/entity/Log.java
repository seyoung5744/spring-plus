package org.example.expert.domain.log.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "logs")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long authUserId;

    private Long todoId;

    private Long managerUserId;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private Log(Long authUserId, Long todoId, Long managerUserId) {
        this.authUserId = authUserId;
        this.todoId = todoId;
        this.managerUserId = managerUserId;
    }

    public static Log create(Long authUserId, Long todoId, Long managerUserId) {
        return Log.builder()
                .authUserId(authUserId)
                .todoId(todoId)
                .managerUserId(managerUserId)
                .build();
    }
}
