package com.technicalchallenge.service;

import com.technicalchallenge.exceptions.referencedata.HolidayCalenderNotFoundException;
import com.technicalchallenge.model.HolidayCalendar;
import com.technicalchallenge.repository.HolidayCalendarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class HolidayCalendarService {
    private static final Logger logger = LoggerFactory.getLogger(HolidayCalendarService.class);

    @Autowired
    private HolidayCalendarRepository holidayCalendarRepository;

    public List<HolidayCalendar> findAll() {
        logger.info("Retrieving all holiday calendars");
        return holidayCalendarRepository.findAll();
    }

    public Optional<HolidayCalendar> findById(Long id) {
        logger.debug("Retrieving holiday calendar by id: {}", id);
        return holidayCalendarRepository.findById(id);
    }

    public Optional<HolidayCalendar> findByHolidayCalendar(String holidayCalendar) {
        logger.debug("Retrieving holiday calendar by holidayCalendar: {}", holidayCalendar);
        return holidayCalendarRepository.findByHolidayCalendar(holidayCalendar);
    }

    public HolidayCalendar save(HolidayCalendar holidayCalendar) {
        logger.info("Saving holiday calendar: {}", holidayCalendar);
        return holidayCalendarRepository.save(holidayCalendar);
    }

    public void deleteById(Long id) {
        logger.warn("Deleting holiday calendar with id: {}", id);
        holidayCalendarRepository.deleteById(id);
    }

    // Checks the Reference Data for TradeLeg Service
    public HolidayCalendar findId(Long id) {
        logger.debug("Retrieving holiday calendar by id: {}", id);
        return holidayCalendarRepository.findById(id)
                .orElseThrow(() -> new HolidayCalenderNotFoundException("holidayCalendarId", id));
    }

    public HolidayCalendar findHolidayCalendar(String holidayCalendar) {
        logger.debug("Retrieving holiday calendar by holidayCalendar: {}", holidayCalendar);
        return holidayCalendarRepository.findByHolidayCalendar(holidayCalendar)
                .orElseThrow(() -> new HolidayCalenderNotFoundException("holidayCalendar", holidayCalendar));
    }
}
