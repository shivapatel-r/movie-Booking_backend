package com.moviebookingapp.api.domain.mappers;

import com.moviebookingapp.api.domain.dtos.SignupRequest;
import com.moviebookingapp.api.domain.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "role", ignore = true)
  User toEntity(SignupRequest dto);
}
