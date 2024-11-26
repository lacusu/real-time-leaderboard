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
public class LeaderboardDto {

    private String userId;

    private String userName;

    private Double score;
}
