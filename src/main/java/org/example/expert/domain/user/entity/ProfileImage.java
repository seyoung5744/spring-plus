package org.example.expert.domain.user.entity;

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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ProfileImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String url; // 파일 경로

    @Column(nullable = false)
    private String originalFileName; // 고객이 업로드한 파일명

    @Column(nullable = false)
    private String storedFileName; // 서버 내부에서 관리하는 파일명

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private String ext;

    @Column(nullable = false)
    private long size;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    private ProfileImage(User user, String url, String originalFileName, String storedFileName, String contentType, String ext, long size) {
        this.user = user;
        this.url = url;
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.contentType = contentType;
        this.ext = ext;
        this.size = size;
    }
}
