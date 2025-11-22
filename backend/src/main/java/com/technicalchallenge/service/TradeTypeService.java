package com.technicalchallenge.service;

import com.technicalchallenge.dto.TradeSubTypeDTO;
import com.technicalchallenge.dto.TradeTypeDTO;
import com.technicalchallenge.exceptions.referencedata.TradeTypeNotFoundException;
import com.technicalchallenge.mapper.TradeSubTypeMapper;
import com.technicalchallenge.mapper.TradeTypeMapper;
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

    // TradeType Mapper
    private final TradeTypeMapper tradeTypeMapper;

    // TradeSubType Mapper
    private final TradeSubTypeMapper tradeSubTypeMapper;

    /**
     * Returns all tradetypes on the system.
     */
    public List<TradeTypeDTO> findAll() {
        logger.info("Retrieving all trade types");
        return tradeTypeRepository.findAll().stream()
                .map(tradeTypeMapper::toDto)
                .toList();
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
    public TradeTypeDTO findById(Long id) {
        logger.debug("Retrieving trade type by id: {}", id);
        TradeType tradeType = findTradeTypeId(id);
        TradeTypeDTO tradeTypeDTO = tradeTypeMapper.toDto(tradeType);
        return tradeTypeDTO;
    }

    public TradeSubTypeDTO getByTradeSubTypeId(Long id) {
        logger.debug("Retrieving trade sub type by id: {}", id);
        TradeSubType tradeSubType = findTradeSubTypeId(id);
        TradeSubTypeDTO tradeSubTypeDTO = tradeSubTypeMapper.toDto(tradeSubType);
        return tradeSubTypeDTO;
    }

    public TradeTypeDTO save(TradeTypeDTO tradeTypeDTO) {
        logger.info("Saving trade type: {}", tradeTypeDTO);
        var entity = tradeTypeMapper.toEntity(tradeTypeDTO);
        logger.debug("Saving TradeType Entity: {}", entity);
        var saved = tradeTypeRepository.save(entity);
        return tradeTypeMapper.toDto(saved);
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
