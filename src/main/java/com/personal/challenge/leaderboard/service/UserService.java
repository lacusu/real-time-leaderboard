package com.personal.challenge.leaderboard.service;

import com.personal.challenge.leaderboard.dto.UserInfo;

import java.io.IOException;
import java.util.Optional;

public interface UserService {
    Optional<UserInfo> getUserInfo(String userId);
}
