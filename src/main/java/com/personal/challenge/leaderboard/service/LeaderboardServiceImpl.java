package com.personal.challenge.leaderboard.service;

import com.personal.challenge.leaderboard.config.AppConfig;
import com.personal.challenge.leaderboard.dto.LeaderboardDto;
import com.personal.challenge.leaderboard.dto.UserInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class LeaderboardServiceImpl implements LeaderboardService {
    private SimpMessagingTemplate messagingTemplate;

    private RedisTemplate<String, String> redisTemplate;

    private AppConfig appConfig;

    private UserService userService;

    @Override
    public String getLeaderboardKey(String quizId) {
        // Create a key per quiz
        return appConfig.getLeaderboard().getKeyPrefix() + quizId;
    }

    @Override
    public void setLeaderboardExpire(String leaderboardKey) {
        try {
            Optional<Long> countExistingKey = Optional.ofNullable(redisTemplate.countExistingKeys(Collections.singleton(leaderboardKey)));
            if (countExistingKey.isPresent() && countExistingKey.get() > 0) {
                redisTemplate.expire(leaderboardKey, Duration.ofMinutes(appConfig.getLeaderboard().getDefaultExpiryTimeMinutes()));
            }
        } catch (Exception exception) {
            log.warn("Failed to set expiry time for leaderboard key: {} due to {}", leaderboardKey, exception.getMessage());
        }

    }

    @Override
    public void handleLeaderboardChanged(String quizId) {
        List<LeaderboardDto> leaderboard = getLeaderboard(quizId);
        if (!leaderboard.isEmpty()) {
            String msgDestination = String.format(appConfig.getLeaderboard().getLeaderboardWSTopic(), quizId);
            log.info("Sending leaderboard for quizId: {}", quizId);
            messagingTemplate.convertAndSend(msgDestination, leaderboard);
        }
    }

    @Override
    public List<LeaderboardDto> getLeaderboard(String quizId) {
        log.info("Getting leaderboard for quizId: {}", quizId);
        String leaderboardKey = getLeaderboardKey(quizId);
        Set<ZSetOperations.TypedTuple<String>> topScores = redisTemplate.opsForZSet().reverseRangeWithScores(leaderboardKey, 0, appConfig.getLeaderboard().getTopNumber() - 1);

        if (topScores != null && !topScores.isEmpty()) {
            return topScores.stream()
                    .map(item -> LeaderboardDto.builder()
                            .userId(item.getValue())
                            .userName(getUserName(item.getValue()))
                            .score(item.getScore())
                            .build())
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private String getUserName(String userId) {
        Optional<UserInfo> userInfoOptional = userService.getUserInfo(userId);
        if (userInfoOptional.isEmpty()) {
            return userId;
        }
        return userInfoOptional.get().getUserName();
    }
}
