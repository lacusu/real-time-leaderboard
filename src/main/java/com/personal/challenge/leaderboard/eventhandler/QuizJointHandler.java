package com.personal.challenge.leaderboard.eventhandler;

import com.personal.challenge.leaderboard.dto.GameScoreDto;
import com.personal.challenge.leaderboard.dto.QuizJoint;
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
public class QuizJointHandler {
    private KafkaTemplate<String, String> kafkaTemplate;

    private LeaderboardService leaderboardService;

    private ScoreUpdatedService scoreUpdatedService;

    @KafkaListener(
            topics = {"${application.config.broker-properties.quiz-joint.topic}"},
            groupId = "${application.config.broker-properties.quiz-joint.group")
    public void consumeQuizJoint(ConsumerRecord<String, String> record) throws IOException {
        QuizJoint quizJoint = ObjectMapperUtil.mapStringToClass(record.value(), QuizJoint.class);
        log.info("User Id {} has joint Quiz Id {}", quizJoint.getUserId(), quizJoint.getQuizId());

        GameScoreDto scoreDto = GameScoreDto.builder()
                .quizId(quizJoint.getQuizId())
                .score(0d) //Init score 0 when joining the quiz
                .userId(quizJoint.getUserId())
                .build();
        scoreUpdatedService.updateScore(scoreDto);

        try {
            leaderboardService.handleLeaderboardChanged(scoreDto.getQuizId());
        } catch (Exception exception) {
            log.warn("Failed to notify leaderboard changed, Will try next time quizId: {}", scoreDto.getQuizId());
        }
    }
}
