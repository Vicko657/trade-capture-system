package com.technicalchallenge.mapper;

import com.technicalchallenge.dto.PayRecDTO;
import com.technicalchallenge.model.PayRec;

import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PayRecMapper {
    private final ModelMapper modelMapper;

    public PayRecDTO toDto(PayRec entity) {
        return modelMapper.map(entity, PayRecDTO.class);
    }

    public PayRec toEntity(PayRecDTO dto) {
        return modelMapper.map(dto, PayRec.class);
    }
}
