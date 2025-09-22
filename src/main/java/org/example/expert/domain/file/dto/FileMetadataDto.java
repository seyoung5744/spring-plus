package org.example.expert.domain.file.dto;

import lombok.Builder;

@Builder
public record FileMetadataDto(
        String url, // 파일 경로
        String originalFileName, // 고객이 업로드한 파일명
        String storedFileName, // 서버 내부에서 관리하는 파일명
        String contentType,
        String extension,
        long fileSize
) {
}
