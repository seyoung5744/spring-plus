package org.example.expert.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class UserResponse {
    private final Long id;
    private final String email;
    private final String nickname;
    private final String profileUr;

    public UserResponse(Long id, String email, String nickname, String profileUr) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.profileUr = profileUr;
    }

    public UserResponse(Long id, String email) {
        this(id, email, null, null);
    }
}
