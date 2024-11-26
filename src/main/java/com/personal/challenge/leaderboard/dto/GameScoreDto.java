package com.personal.challenge.leaderboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
public class GameScoreDto {
    private String quizId;

    private String userId;

    private Double score;
}
