package com.technicalchallenge.mapper;

import com.technicalchallenge.dto.IndexDTO;
import com.technicalchallenge.model.Index;

import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IndexMapper {

    private final ModelMapper modelMapper;

    public IndexDTO toDto(Index entity) {
        return modelMapper.map(entity, IndexDTO.class);
    }

    public Index toEntity(IndexDTO dto) {
        return modelMapper.map(dto, Index.class);
    }
}
