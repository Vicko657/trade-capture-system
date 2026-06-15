package com.technicalchallenge.mapper;

import com.technicalchallenge.dto.TradeSubTypeDTO;
import com.technicalchallenge.model.TradeSubType;

import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TradeSubTypeMapper {

    private final ModelMapper modelMapper;

    public TradeSubTypeDTO toDto(TradeSubType entity) {
        return modelMapper.map(entity, TradeSubTypeDTO.class);
    }

    public TradeSubType toEntity(TradeSubTypeDTO dto) {
        return modelMapper.map(dto, TradeSubType.class);
    }
}
