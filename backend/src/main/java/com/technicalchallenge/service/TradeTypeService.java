package com.technicalchallenge.service;

import com.technicalchallenge.exceptions.referencedata.TradeTypeNotFoundException;
import com.technicalchallenge.model.TradeSubType;
import com.technicalchallenge.model.TradeType;
import com.technicalchallenge.repository.TradeSubTypeRepository;
import com.technicalchallenge.repository.TradeTypeRepository;

import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * TradeType service class provides business logic and operations relating to
 * types and subtypes of trades.
 */
@Service
@AllArgsConstructor
public class TradeTypeService {

    private static final Logger logger = LoggerFactory.getLogger(TradeTypeService.class);

    // TradeType CRUD Interface
    @Autowired
    private TradeTypeRepository tradeTypeRepository;

    // TradeSubType CRUD Interface
    @Autowired
    private TradeSubTypeRepository tradeSubTypeRepository;

    /**
     * Returns all tradetypes on the system.
     */
    public List<TradeType> findAll() {
        logger.info("Retrieving all trade types");
        return tradeTypeRepository.findAll();
    }

    /**
     * Returns a tradetype using their tradetypeId.
     * 
     * <p>
     * This method is used to retrieve a specific tradetype
     * </p>
     * 
     * @param id tradetype's unique identifier
     * @throws TradeTypeNotFoundException if the tradetype is not found
     */
    public Optional<TradeType> findById(Long id) {
        logger.debug("Retrieving trade type by id: {}", id);
        return tradeTypeRepository.findById(id);
    }

    public Optional<TradeSubType> getByTradeSubTypeId(Long id) {
        logger.debug("Retrieving trade sub type by id: {}", id);
        return tradeSubTypeRepository.findById(id);
    }

    public TradeType save(TradeType tradeType) {
        return tradeTypeRepository.save(tradeType);
    }

    public void deleteById(Long id) {
        logger.warn("Deleting trade type with id: {}", id);
        TradeType tradeType = findTradeTypeId(id);
        tradeTypeRepository.delete(tradeType);
    }

    // Checks the Reference Data for Trade Service
    public TradeType findTradeTypeId(Long id) {
        return tradeTypeRepository.findById(id)
                .orElseThrow(() -> new TradeTypeNotFoundException("tradeTypeId", id));
    }

    public TradeType findTradeType(String type) {
        logger.debug("Retrieving trade type by type: {}", type);
        return tradeTypeRepository.findByTradeType(type)
                .orElseThrow(() -> new TradeTypeNotFoundException("tradetype", type));
    }

    public TradeSubType findTradeSubTypeId(Long id) {
        return tradeSubTypeRepository.findById(id)
                .orElseThrow(() -> new TradeTypeNotFoundException("tradeSubTypeId", id));
    }

    public TradeSubType findTradeSubType(String subType) {
        logger.debug("Retrieving trade sub type by type: {}", subType);
        return tradeSubTypeRepository.findByTradeSubType(subType)
                .orElseThrow(() -> new TradeTypeNotFoundException("tradesubtype", subType));

    }

}
