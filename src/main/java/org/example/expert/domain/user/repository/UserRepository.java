package org.example.expert.domain.user.repository;

import org.example.expert.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findAllByNickname(String nickname);

    @Query(value = "select * from users ignore index(idx_users_nickname) where nickname = :nickname", nativeQuery = true)
    List<User> findAllByNicknameIgnoreIndex(String nickname);
}
