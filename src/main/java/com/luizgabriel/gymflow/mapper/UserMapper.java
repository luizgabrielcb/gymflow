package com.luizgabriel.gymflow.mapper;

import com.luizgabriel.gymflow.domain.User;
import com.luizgabriel.gymflow.dto.response.UserGetResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserGetResponse toUserGetResponse(User user);
}
