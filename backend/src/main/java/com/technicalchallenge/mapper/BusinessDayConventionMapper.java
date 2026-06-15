package com.technicalchallenge.mapper;

import com.technicalchallenge.dto.BusinessDayConventionDTO;
import com.technicalchallenge.model.BusinessDayConvention;

import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BusinessDayConventionMapper {
    private final ModelMapper modelMapper;

    public BusinessDayConventionDTO toDto(BusinessDayConvention entity) {
        return modelMapper.map(entity, BusinessDayConventionDTO.class);
    }

    public BusinessDayConvention toEntity(BusinessDayConventionDTO dto) {
        return modelMapper.map(dto, BusinessDayConvention.class);
    }
}
