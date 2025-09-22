package org.example.expert.domain.file.uploder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.common.exception.ServerException;
import org.example.expert.domain.file.dto.FileInfo;
import org.example.expert.domain.file.dto.FileMetadataDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;

@Slf4j
@Component
@Profile("prod")
@RequiredArgsConstructor
public class S3FileUploader extends AbstractFileUploader {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final S3Client s3Client;

    @Override
    protected FileMetadataDto store(MultipartFile file, FileInfo fileInfo) {
        // 이미지 파일 -> InputStream 변환
        try (InputStream inputStream = file.getInputStream()) {
            // PutObjectRequest 객체 생성
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket) // 버킷 이름
                    .key(fileInfo.storedFileName()) // 저장할 파일 이름
                    .acl(ObjectCannedACL.PUBLIC_READ) // 퍼블릭 읽기 권한
                    .contentType(fileInfo.contentType()) // 이미지 MIME 타입
                    .contentLength(fileInfo.fileSize()) // 파일 크기
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, file.getSize())); // S3에 이미지 업로드
        } catch (Exception e) {
            log.warn("Store Profile Failed: {}", e.getMessage());
            throw new ServerException("파일 업로드에 실패했습니다.");
        }

        return FileMetadataDto.builder()
                .url(s3Client.utilities().getUrl(url -> url.bucket(bucket).key(fileInfo.storedFileName())).toString())
                .originalFileName(fileInfo.originalFilename())
                .storedFileName(fileInfo.storedFileName())
                .contentType(fileInfo.contentType())
                .extension(fileInfo.extension())
                .fileSize(fileInfo.fileSize())
                .build();
    }

}
