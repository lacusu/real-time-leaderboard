package com.personal.challenge.leaderboard.service;

import com.personal.challenge.leaderboard.config.AppConfig;
import com.personal.challenge.leaderboard.dto.GameScoreDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class ScoreUpdatedServerImpl implements ScoreUpdatedService {

    private RedisTemplate<String, String> redisTemplate;

    private LeaderboardService leaderboardService;

    @Override
    public void updateScore(GameScoreDto scoreDto) {
        log.info("Updating leaderboard due to score updated for quizId: {}, userId {}", scoreDto.getQuizId(), scoreDto.getUserId());
        String leaderboardKey = leaderboardService.getLeaderboardKey(scoreDto.getQuizId());
        redisTemplate.opsForZSet().incrementScore(leaderboardKey, scoreDto.getUserId().toString(), scoreDto.getScore());

        leaderboardService.setLeaderboardExpire(leaderboardKey);


    }


}
