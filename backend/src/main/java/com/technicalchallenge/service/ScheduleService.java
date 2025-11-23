package com.technicalchallenge.service;

import com.technicalchallenge.exceptions.referencedata.ScheduleNotFoundException;
import com.technicalchallenge.model.Schedule;
import com.technicalchallenge.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class ScheduleService {
    private static final Logger logger = LoggerFactory.getLogger(ScheduleService.class);

    @Autowired
    private ScheduleRepository scheduleRepository;

    public List<Schedule> findAll() {
        logger.info("Retrieving all schedules");
        return scheduleRepository.findAll();
    }

    public Optional<Schedule> findById(Long id) {
        logger.debug("Retrieving schedule by id: {}", id);
        return scheduleRepository.findById(id);
    }

    public Optional<Schedule> findBySchedule(String schduele) {
        logger.debug("Retrieving schedule by schedule: {}", schduele);
        return scheduleRepository.findBySchedule(schduele);
    }

    public Schedule save(Schedule schedule) {
        logger.info("Saving schedule: {}", schedule);
        return scheduleRepository.save(schedule);
    }

    public void deleteById(Long id) {
        logger.warn("Deleting schedule with id: {}", id);
        scheduleRepository.deleteById(id);
    }

    // Checks the Reference Data for TradeLeg Service
    public Schedule findId(Long id) {
        logger.debug("Retrieving schedule by id: {}", id);
        return scheduleRepository.findById(id).orElseThrow(() -> new ScheduleNotFoundException("scheduleId", id));
    }

    public Schedule findSchedule(String schedule) {
        logger.debug("Retrieving schedule by schedule: {}", schedule);
        return scheduleRepository.findBySchedule(schedule)
                .orElseThrow(() -> new ScheduleNotFoundException("schedule", schedule));
    }
}
