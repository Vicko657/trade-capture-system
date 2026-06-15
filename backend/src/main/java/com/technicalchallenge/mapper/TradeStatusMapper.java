package com.technicalchallenge.mapper;

import com.technicalchallenge.dto.TradeStatusDTO;
import com.technicalchallenge.model.TradeStatus;

import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TradeStatusMapper {
    private final ModelMapper modelMapper;

    public TradeStatusDTO toDto(TradeStatus entity) {
        return modelMapper.map(entity, TradeStatusDTO.class);
    }

    public TradeStatus toEntity(TradeStatusDTO dto) {
        return modelMapper.map(dto, TradeStatus.class);
    }
}
