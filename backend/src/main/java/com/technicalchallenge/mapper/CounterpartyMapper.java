package com.technicalchallenge.mapper;

import com.technicalchallenge.dto.CounterpartyDTO;
import com.technicalchallenge.model.Counterparty;

import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CounterpartyMapper {

    private final ModelMapper modelMapper;

    public CounterpartyDTO toDto(Counterparty entity) {
        return modelMapper.map(entity, CounterpartyDTO.class);
    }

    public Counterparty toEntity(CounterpartyDTO dto) {
        return modelMapper.map(dto, Counterparty.class);
    }
}
