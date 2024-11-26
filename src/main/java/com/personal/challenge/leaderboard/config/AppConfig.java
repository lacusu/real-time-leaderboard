package com.personal.challenge.leaderboard.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "application.config")
@Getter
@Setter
@NoArgsConstructor
public class AppConfig {

    private Leaderboard leaderboard;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Leaderboard {
        private String KeyPrefix;

        private int defaultExpiryTimeMinutes;

        private int topNumber;

        private String leaderboardWSTopic;
    }
}
