package org.example.expert.bulk;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@SpringBootTest
@ActiveProfiles("local")
class BulkInsertTest {

    private static final int TOTAL_USERS = 5_000_000; // 총 500만 건
    private static final int BATCH_SIZE = 5_000;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void bulkInsertDummyUsers() {
        long startTime = System.currentTimeMillis();
        Random random = new Random();

        List<UserCreateDto> dummies = new ArrayList<>(BATCH_SIZE);

        Set<String> nicknameSet = new HashSet<>();

        for (int i = 1; i <= TOTAL_USERS; i++) {
            String nickname;
            do {
                nickname = randomNickname(random);
            } while (!nicknameSet.add(nickname));

            UserCreateDto user = new UserCreateDto(
                    "user" + i + "@example.com",
                    nickname,
                    "password123",
                    "USER",
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );
            dummies.add(user);

            if (i % BATCH_SIZE == 0) {
                insertBatch(dummies);
                dummies.clear();
                nicknameSet.clear(); // 배치 단위 중복 제거용 Set 초기화
                System.out.println(i + " users inserted");
            }
        }

        if (!dummies.isEmpty()) {
            insertBatch(dummies);
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Total insert time: " + (endTime - startTime) / 1000 + " sec");
    }

    private void insertBatch(List<UserCreateDto> batch) {
        String SQL = "INSERT INTO users (email, nickname, password, user_role, created_at, modified_at) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(SQL, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                UserCreateDto user = batch.get(i);
                ps.setString(1, user.getEmail());
                ps.setString(2, user.getNickname());
                ps.setString(3, user.getPassword());
                ps.setString(4, user.getUserRole());
                ps.setTimestamp(5, Timestamp.valueOf(user.getCreatedAt()));
                ps.setTimestamp(6, Timestamp.valueOf(user.getModifiedAt()));
            }

            @Override
            public int getBatchSize() {
                return batch.size();
            }
        });
    }

    private String randomNickname(Random random) {
        return "user" + random.nextInt(Integer.MAX_VALUE);
    }
}