package com.personal.challenge.leaderboard.service;

import com.personal.challenge.leaderboard.connector.UserServiceConnector;
import com.personal.challenge.leaderboard.connector.dto.APIResponse;
import com.personal.challenge.leaderboard.connector.dto.UserInfoAPIResponse;
import com.personal.challenge.leaderboard.dto.UserInfo;
import com.personal.challenge.leaderboard.mapper.UserMapper;
import com.personal.challenge.leaderboard.util.ObjectMapperUtil;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private UserServiceConnector userServiceConnector;

    private RedisTemplate<String, String> redisTemplate;

    @Override
    public Optional<UserInfo> getUserInfo(String userId) {
        String cacheKey = "user-info-" + userId;

        try {
            // Check if user info is present in cache
            String cachedUserInfoString = redisTemplate.opsForValue().get(cacheKey);
            if (StringUtils.isNotBlank(cachedUserInfoString)) {
                UserInfo cachedUserInfo = ObjectMapperUtil.mapStringToClass(cachedUserInfoString, UserInfo.class);
                if (Objects.nonNull(cachedUserInfo)) {
                    return Optional.of(cachedUserInfo);

                }
            }
        } catch (Exception exception) {
            log.warn("Failed to parse user info from cached: {} due to {}. Ignore!", cacheKey, exception.getMessage());
        }

        // Fetch user info from user service
        UserInfo userInfo = fetchUserInfo(userId);

        if (Objects.nonNull(userInfo)) {  // Cache the user info only if found
            try {
                String userInfoString = ObjectMapperUtil.writeValueAsString(userInfo);
                redisTemplate.opsForValue().set(cacheKey, userInfoString, Duration.ofSeconds(60));
            } catch (Exception exception) {
                log.warn("Failed to cached the user info, user id {} due to {}", userInfo.getUserId(), exception.getMessage());
            }
        }
        return Optional.ofNullable(userInfo);

    }

    private UserInfo fetchUserInfo(String userId) {
        APIResponse<UserInfoAPIResponse> APIResponse = userServiceConnector.getUserInfo(userId);
        if (Objects.nonNull(APIResponse)) {
            UserInfoAPIResponse userInfoAPIResponse = APIResponse.getResult();
            return UserMapper.INSTANCE.fromAPIResponse(userInfoAPIResponse);
        }
        return new UserInfo();
    }
}
