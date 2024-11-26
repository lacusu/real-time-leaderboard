package com.personal.challenge.leaderboard.connector.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UserInfoAPIResponse {
    private String userId;

    private String userName;

    //More fields add in the real case
}
