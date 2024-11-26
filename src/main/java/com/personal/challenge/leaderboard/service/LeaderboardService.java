package com.personal.challenge.leaderboard.service;

import com.personal.challenge.leaderboard.dto.LeaderboardDto;

import java.util.List;

public interface LeaderboardService {
    String getLeaderboardKey(String quizId);

    void setLeaderboardExpire(String leaderboardKey);

    void handleLeaderboardChanged(String quizId);

    List<LeaderboardDto> getLeaderboard(String quizId);
}
