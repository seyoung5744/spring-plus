package org.example.expert.domain.file.uploder;

import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.common.exception.ServerException;
import org.example.expert.domain.file.dto.FileInfo;
import org.example.expert.domain.file.dto.FileMetadataDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Component
@Profile("local")
public class LocalFileUploader extends AbstractFileUploader {

    private final Path uploadedPath;

    public LocalFileUploader(@Value("${profile.location}") String uploadedPath) {
        this.uploadedPath = Path.of(uploadedPath).toAbsolutePath().normalize();
        log.info("Upload path: {}", this.uploadedPath);

        try {
            Files.createDirectory(this.uploadedPath);
        } catch (IOException e) {
            log.error(uploadedPath + " Directory already exists : {}", e.getMessage());
        }
    }

    @Override
    protected FileMetadataDto store(MultipartFile file, FileInfo fileInfo) {
        try {
            Path dirPath = this.uploadedPath.resolve(fileInfo.storedFileName()).getParent();
            Files.createDirectories(dirPath);

            file.transferTo(this.uploadedPath.resolve(fileInfo.storedFileName()));
        } catch (IOException e) {
            log.warn("Store Profile Failed: {}", e.getMessage());
            throw new ServerException("파일 업로드에 실패했습니다.");
        }

        return FileMetadataDto.builder()
                .url(this.uploadedPath + "/" + fileInfo.storedFileName())
                .originalFileName(fileInfo.originalFilename())
                .storedFileName(fileInfo.storedFileName())
                .contentType(fileInfo.contentType())
                .extension(fileInfo.extension())
                .fileSize(fileInfo.fileSize())
                .build();
    }

}
