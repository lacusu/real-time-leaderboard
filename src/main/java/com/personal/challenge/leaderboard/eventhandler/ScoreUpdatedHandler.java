package com.personal.challenge.leaderboard.eventhandler;

import com.personal.challenge.leaderboard.dto.GameScoreDto;
import com.personal.challenge.leaderboard.service.LeaderboardService;
import com.personal.challenge.leaderboard.service.ScoreUpdatedService;
import com.personal.challenge.leaderboard.util.ObjectMapperUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@AllArgsConstructor
public class ScoreUpdatedHandler {
    private final KafkaTemplate<String, String> kafkaLeaderboardChangeTemplate;

    private final ScoreUpdatedService scoreUpdatedService;

    private final LeaderboardService leaderboardService;

    @KafkaListener(
            topics = {"${application.config.broker-properties.game-score.topic}"},
            groupId = "${application.config.broker-properties.game-score.group")
    public void handleScoreUpdated(ConsumerRecord<String, String> record) throws IOException {
        GameScoreDto scoreDto = ObjectMapperUtil.mapStringToClass(record.value(), GameScoreDto.class);
        log.info("record consumed quizId: {}, userId: {}, score: {}", scoreDto.getQuizId(), scoreDto.getUserId(),
                scoreDto.getScore());

        scoreUpdatedService.updateScore(scoreDto);

        try {
            leaderboardService.handleLeaderboardChanged(scoreDto.getQuizId());
        } catch (Exception exception) {
            log.warn("Failed to notify leaderboard changed, Will try next time quizId: {}", scoreDto.getQuizId());
        }
    }
}
