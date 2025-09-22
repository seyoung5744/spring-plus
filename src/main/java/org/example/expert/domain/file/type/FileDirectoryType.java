package org.example.expert.domain.file.type;

import lombok.Getter;

@Getter
public enum FileDirectoryType {

    PROFILE("profile");

    private final String directory;

    FileDirectoryType(String directory) {
        this.directory = directory;
    }
}
