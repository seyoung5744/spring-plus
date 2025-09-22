package org.example.expert.domain.file.uploder;

import org.example.expert.domain.file.dto.FileMetadataDto;
import org.example.expert.domain.file.type.FileDirectoryType;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploader {

    FileMetadataDto upload(MultipartFile file, FileDirectoryType directory);

}
