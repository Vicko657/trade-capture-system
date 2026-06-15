package com.technicalchallenge.mapper;

import com.technicalchallenge.dto.TradeTypeDTO;
import com.technicalchallenge.model.TradeType;

import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TradeTypeMapper {
    private final ModelMapper modelMapper;

    public TradeTypeDTO toDto(TradeType entity) {
        return modelMapper.map(entity, TradeTypeDTO.class);
    }

    public TradeType toEntity(TradeTypeDTO dto) {
        return modelMapper.map(dto, TradeType.class);
    }
}
