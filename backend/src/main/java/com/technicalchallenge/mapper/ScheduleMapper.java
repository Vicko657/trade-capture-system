package com.technicalchallenge.mapper;

import com.technicalchallenge.dto.ScheduleDTO;
import com.technicalchallenge.model.Schedule;

import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduleMapper {

    private final ModelMapper modelMapper;

    public ScheduleDTO toDto(Schedule entity) {
        return modelMapper.map(entity, ScheduleDTO.class);
    }

    public Schedule toEntity(ScheduleDTO dto) {
        return modelMapper.map(dto, Schedule.class);
    }
}
