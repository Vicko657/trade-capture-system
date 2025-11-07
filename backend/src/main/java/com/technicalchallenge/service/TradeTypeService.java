package com.technicalchallenge.service;

import com.technicalchallenge.exceptions.EntityNotFoundException;
import com.technicalchallenge.model.TradeSubType;
import com.technicalchallenge.model.TradeType;
import com.technicalchallenge.repository.TradeSubTypeRepository;
import com.technicalchallenge.repository.TradeTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class TradeTypeService {

    private static final Logger logger = LoggerFactory.getLogger(TradeTypeService.class);

    @Autowired
    private TradeTypeRepository tradeTypeRepository;

    @Autowired
    private TradeSubTypeRepository tradeSubTypeRepository;

    public List<TradeType> findAll() {
        logger.info("Retrieving all trade types");
        return tradeTypeRepository.findAll();
    }

    public Optional<TradeType> findById(Long id) {
        logger.debug("Retrieving trade type by id: {}", id);
        return tradeTypeRepository.findById(id);
    }

    public Optional<TradeType> findByTradeType(String tradeType) {
        logger.debug("Retrieving trade type by type: {}", tradeType);
        return tradeTypeRepository.findByTradeType(tradeType);
    }

    public Optional<TradeSubType> findByTradeSubTypeId(Long id) {
        logger.debug("Retrieving trade sub type by id: {}", id);
        return tradeSubTypeRepository.findById(id);
    }

    public Optional<TradeSubType> findByTradeSubType(String tradeSubType) {
        logger.debug("Retrieving trade sub type by type: {}", tradeSubType);
        return tradeSubTypeRepository.findByTradeSubType(tradeSubType);
    }

    public void validateTradeType(Long id, String tradeType) {

        if (id != null) {
            if (findById(id).isEmpty()) {
                throw new EntityNotFoundException("TradeType not found by id");
            }
        } else if (tradeType != null) {
            if (findByTradeType(tradeType).isEmpty()) {
                throw new EntityNotFoundException("TradeType not found by trade type");
            }
        }

    }

    public void validateTradeSubType(Long id, String tradeSubType) {

        if (id != null) {
            if (findByTradeSubTypeId(id).isEmpty()) {
                throw new EntityNotFoundException("TradeSubType not found by id");
            }
        } else if (tradeSubType != null) {
            if (findByTradeType(tradeSubType).isEmpty()) {
                throw new EntityNotFoundException("TradeSubType not found by trade sub type");
            }
        }

    }

    public TradeType save(TradeType tradeType) {
        logger.info("Saving trade type: {}", tradeType);
        return tradeTypeRepository.save(tradeType);
    }

    public void deleteById(Long id) {
        logger.warn("Deleting trade type with id: {}", id);
        tradeTypeRepository.deleteById(id);
    }
}
