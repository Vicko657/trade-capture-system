package com.technicalchallenge.mapper;

import com.technicalchallenge.dto.UserProfileDTO;
import com.technicalchallenge.model.UserProfile;

import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserProfileMapper {
    private final ModelMapper modelMapper;

    public UserProfileDTO toDto(UserProfile entity) {
        return modelMapper.map(entity, UserProfileDTO.class);
    }

    public UserProfile toEntity(UserProfileDTO dto) {
        return modelMapper.map(dto, UserProfile.class);
    }
}
