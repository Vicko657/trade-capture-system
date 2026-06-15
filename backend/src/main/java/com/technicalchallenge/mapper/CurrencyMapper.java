package com.technicalchallenge.mapper;

import com.technicalchallenge.dto.CurrencyDTO;
import com.technicalchallenge.model.Currency;

import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrencyMapper {
    private final ModelMapper modelMapper;

    public CurrencyDTO toDto(Currency entity) {
        return modelMapper.map(entity, CurrencyDTO.class);
    }

    public Currency toEntity(CurrencyDTO dto) {
        return modelMapper.map(dto, Currency.class);
    }
}
