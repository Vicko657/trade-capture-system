package com.technicalchallenge.mapper;

import com.technicalchallenge.dto.UserPrivilegeDTO;
import com.technicalchallenge.model.UserPrivilege;

import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserPrivilegeMapper {

    private final ModelMapper modelMapper;

    public UserPrivilegeDTO toDto(UserPrivilege entity) {
        return modelMapper.map(entity, UserPrivilegeDTO.class);
    }

    public UserPrivilege toEntity(UserPrivilegeDTO dto) {
        return modelMapper.map(dto, UserPrivilege.class);
    }
}
