package com.technicalchallenge.mapper;

import com.technicalchallenge.dto.PrivilegeDTO;
import com.technicalchallenge.model.Privilege;

import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PrivilegeMapper {

    private final ModelMapper modelMapper;

    public PrivilegeDTO toDto(Privilege entity) {
        return modelMapper.map(entity, PrivilegeDTO.class);
    }

    public Privilege toEntity(PrivilegeDTO dto) {
        return modelMapper.map(dto, Privilege.class);
    }
}
