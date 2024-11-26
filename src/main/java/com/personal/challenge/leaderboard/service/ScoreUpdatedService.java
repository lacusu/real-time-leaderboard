package com.personal.challenge.leaderboard.service;

import com.personal.challenge.leaderboard.dto.GameScoreDto;

public interface ScoreUpdatedService {

    void updateScore(GameScoreDto scoreDto);
}
