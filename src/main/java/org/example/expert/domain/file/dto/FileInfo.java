package org.example.expert.domain.file.dto;

import lombok.Builder;

@Builder
public record FileInfo(
        String originalFilename,
        String storedFileName,
        String contentType,
        String extension,
        long fileSize
) {
}
