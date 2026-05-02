package com.luizgabriel.gymflow.mapper;

import com.luizgabriel.gymflow.domain.User;
import com.luizgabriel.gymflow.dto.response.UserGetResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    List<UserGetResponse> toUserGetResponseList(List<User> user);

    UserGetResponse toUserGetResponse(User user);
}
