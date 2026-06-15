package com.technicalchallenge.mapper;

import com.technicalchallenge.dto.LegTypeDTO;
import com.technicalchallenge.model.LegType;

import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LegTypeMapper {

    private final ModelMapper modelMapper;

    public LegTypeDTO toDto(LegType entity) {
        return modelMapper.map(entity, LegTypeDTO.class);
    }

    public LegType toEntity(LegTypeDTO dto) {
        return modelMapper.map(dto, LegType.class);
    }
}
