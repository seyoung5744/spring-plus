package org.example.expert.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.file.dto.FileMetadataDto;
import org.example.expert.domain.file.type.FileDirectoryType;
import org.example.expert.domain.file.uploder.FileUploader;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.ProfileImage;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.ProfileImageRepository;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final FileUploader fileUploader;
    private final ProfileImageRepository profileImageRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse getUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new InvalidRequestException("User not found"));
        ProfileImage profileImage = profileImageRepository.findFirstByUserOrderByCreatedAtDesc(user).orElse(null);
        return new UserResponse(user.getId(), user.getEmail(), user.getNickname(),
                profileImage != null ? profileImage.getUrl() : null);
    }

    @Transactional
    public void changePassword(long userId, UserChangePasswordRequest userChangePasswordRequest) {
        validateNewPassword(userChangePasswordRequest);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidRequestException("User not found"));

        if (passwordEncoder.matches(userChangePasswordRequest.getNewPassword(), user.getPassword())) {
            throw new InvalidRequestException("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.");
        }

        if (!passwordEncoder.matches(userChangePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new InvalidRequestException("잘못된 비밀번호입니다.");
        }

        user.changePassword(passwordEncoder.encode(userChangePasswordRequest.getNewPassword()));
    }

    @Transactional
    public void changeProfile(Long userId, MultipartFile profile) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidRequestException("User not found"));

        if (profile != null && !profile.isEmpty()) {
            FileMetadataDto fileMetadataDto = fileUploader.upload(profile, FileDirectoryType.PROFILE);
            profileImageRepository.save(
                    ProfileImage.builder()
                            .user(user)
                            .url(fileMetadataDto.url())
                            .originalFileName(fileMetadataDto.originalFileName())
                            .storedFileName(fileMetadataDto.storedFileName())
                            .contentType(fileMetadataDto.contentType())
                            .ext(fileMetadataDto.extension())
                            .size(fileMetadataDto.fileSize())
                            .build()
            );
        }
    }

    private static void validateNewPassword(UserChangePasswordRequest userChangePasswordRequest) {
        if (userChangePasswordRequest.getNewPassword().length() < 8 ||
                !userChangePasswordRequest.getNewPassword().matches(".*\\d.*") ||
                !userChangePasswordRequest.getNewPassword().matches(".*[A-Z].*")) {
            throw new InvalidRequestException("새 비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.");
        }
    }
}
