package org.example.expert.domain.file.uploder;

import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.file.dto.FileInfo;
import org.example.expert.domain.file.dto.FileMetadataDto;
import org.example.expert.domain.file.type.FileDirectoryType;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public abstract class AbstractFileUploader implements FileUploader {

    private static final String FILE_EXTENSION_SEPARATOR = ".";

    @Override
    public FileMetadataDto upload(MultipartFile file, FileDirectoryType directory) {
        validateFile(file.getOriginalFilename());
        FileInfo fileInfo = buildFileInfo(file, directory);
        return store(file, fileInfo);
    }

    // 저장은 구현체에서만
    protected abstract FileMetadataDto store(MultipartFile file, FileInfo fileInfo);

    // [private 메서드] 파일 유효성 검증
    protected void validateFile(String filename) {
        // 파일 존재 유무 검증
        if (filename == null || filename.isEmpty()) {
            throw new InvalidRequestException("존재하지 않는 파일입니다.");
        }

        // 확장자 존재 유무 검증
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            throw new InvalidRequestException("유효하지 않거나 지원되지 않는 파일 확장자입니다.");
        }

        // 허용되지 않는 확장자 검증
        String extension = URLConnection.guessContentTypeFromName(filename);
        List<String> allowedExtentionList = Arrays.asList("image/jpg", "image/jpeg", "image/png", "image/gif");
        if (extension == null || !allowedExtentionList.contains(extension)) {
            throw new InvalidRequestException("잘못된 파일 확장자입니다.");
        }
    }

    // 공통 FileInfo 생성
    protected FileInfo buildFileInfo(MultipartFile file, FileDirectoryType directory) {
        String originalFilename = file.getOriginalFilename();
        String extension = extractExt(originalFilename);
        String storedFileName = createStoredFileName(directory, originalFilename);
        String contentType = "image/" + extension;
        long fileSize = file.getSize();

        return FileInfo.builder()
                .originalFilename(originalFilename)
                .storedFileName(storedFileName)
                .contentType(contentType)
                .extension(extension)
                .fileSize(fileSize)
                .build();
    }

    protected String createStoredFileName(FileDirectoryType directory, String originalFilename) {
        String uuid = UUID.randomUUID().toString();
        return directory + "/" + uuid + "_" + originalFilename;
    }

    protected String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        return originalFilename.substring(pos + 1);
    }
}
