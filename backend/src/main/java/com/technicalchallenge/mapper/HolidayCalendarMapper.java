package com.technicalchallenge.mapper;

import com.technicalchallenge.dto.HolidayCalendarDTO;
import com.technicalchallenge.model.HolidayCalendar;

import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HolidayCalendarMapper {

    private final ModelMapper modelMapper;

    public HolidayCalendarDTO toDto(HolidayCalendar entity) {
        return modelMapper.map(entity, HolidayCalendarDTO.class);
    }

    public HolidayCalendar toEntity(HolidayCalendarDTO dto) {
        return modelMapper.map(dto, HolidayCalendar.class);
    }
}
