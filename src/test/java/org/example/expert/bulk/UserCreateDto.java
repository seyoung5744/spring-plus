package org.example.expert.bulk;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserCreateDto {

    private String email;
    private String nickname;
    private String password;
    private String userRole;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
