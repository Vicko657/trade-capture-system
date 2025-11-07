package com.technicalchallenge.service;

import com.technicalchallenge.exceptions.EntityNotFoundException;
import com.technicalchallenge.model.BusinessDayConvention;
import com.technicalchallenge.repository.BusinessDayConventionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class BusinessDayConventionService {
    private static final Logger logger = LoggerFactory.getLogger(BusinessDayConventionService.class);

    @Autowired
    private BusinessDayConventionRepository businessDayConventionRepository;

    public List<BusinessDayConvention> findAll() {
        logger.info("Retrieving all business day conventions");
        return businessDayConventionRepository.findAll();
    }

    public Optional<BusinessDayConvention> findById(Long id) {
        logger.debug("Retrieving business day convention by id: {}", id);
        return businessDayConventionRepository.findById(id);
    }

    public Optional<BusinessDayConvention> findByBDC(String bDC) {
        logger.debug("Retrieving business day convention by bdc: {}", bDC);
        return businessDayConventionRepository.findByBdc(bDC);
    }

    public void validateBusinessDayConvention(Long id, String bDC) {

        if (id != null && bDC != null) {

            if (findById(id).isEmpty()) {
                throw new EntityNotFoundException("BusinessDayConvention not found by id");
            } else if (findByBDC(bDC).isEmpty()) {
                throw new EntityNotFoundException("BusinessDayConvention not found by BDC");
            }
        }

    }

    public BusinessDayConvention save(BusinessDayConvention businessDayConvention) {
        logger.info("Saving business day convention: {}", businessDayConvention);
        return businessDayConventionRepository.save(businessDayConvention);
    }

    public void deleteById(Long id) {
        logger.warn("Deleting business day convention with id: {}", id);
        businessDayConventionRepository.deleteById(id);
    }
}
