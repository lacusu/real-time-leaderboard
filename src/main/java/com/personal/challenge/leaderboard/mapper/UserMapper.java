package com.personal.challenge.leaderboard.mapper;


import com.personal.challenge.leaderboard.connector.dto.UserInfoAPIResponse;
import com.personal.challenge.leaderboard.dto.UserInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserInfo fromAPIResponse(UserInfoAPIResponse userInfoAPIResponse);
}
