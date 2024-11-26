package com.personal.challenge.leaderboard.controller;

import com.personal.challenge.leaderboard.connector.dto.APIResponse;
import com.personal.challenge.leaderboard.dto.LeaderboardDto;
import com.personal.challenge.leaderboard.service.LeaderboardService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.personal.challenge.leaderboard.controller.APIEndpoint.API_ROOT;

@Slf4j
@RestController()
@RequestMapping(API_ROOT)
@AllArgsConstructor
public class LeaderboardController {
    private LeaderboardService leaderboardService;

    @GetMapping("/{quizId}")
    public ResponseEntity<APIResponse> getLeaderboard(@PathVariable String quizId) {
        List<LeaderboardDto> leaderboard = leaderboardService.getLeaderboard(quizId);
        return ResponseEntity.ok(APIResponse.builder().result(leaderboard).build()); // 200 OK with leaderboard data

    }
}
