package com.personal.challenge.leaderboard.connector;

import com.personal.challenge.leaderboard.connector.dto.APIResponse;
import com.personal.challenge.leaderboard.connector.dto.UserInfoAPIResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${application.config.connector.user-service}")
public interface UserServiceConnector {
    @GetMapping(value = "/user/{userId}")
    APIResponse<UserInfoAPIResponse> getUserInfo(@PathVariable("userId") String userId);
}
