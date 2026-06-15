package com.technicalchallenge.mapper;

import com.technicalchallenge.dto.DeskDTO;
import com.technicalchallenge.model.Desk;

import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeskMapper {

    private final ModelMapper modelMapper;

    public DeskDTO toDto(Desk entity) {
        return modelMapper.map(entity, DeskDTO.class);
    }

    public Desk toEntity(DeskDTO dto) {
        return modelMapper.map(dto, Desk.class);
    }
}
