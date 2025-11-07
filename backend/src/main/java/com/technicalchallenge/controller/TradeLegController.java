package com.technicalchallenge.controller;

import com.technicalchallenge.dto.TradeLegDTO;
import com.technicalchallenge.mapper.TradeLegMapper;
import com.technicalchallenge.service.TradeLegService;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/tradeLegs")
@Validated
@Tag(name = "TradeLeg", description = "Component or part of a larger trade, which represnts one side or segment of a muilt part transcation")
public class TradeLegController {
    private static final Logger logger = LoggerFactory.getLogger(TradeLegController.class);

    @Autowired
    private TradeLegService tradeLegService;
    @Autowired
    private TradeLegMapper tradeLegMapper;

    @GetMapping
    public List<TradeLegDTO> getAllTradeLegs() {
        logger.info("Fetching all trade legs");
        return tradeLegService.getAllTradeLegs().stream()
                .map(tradeLegMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TradeLegDTO> getTradeLegById(@PathVariable(name = "id") Long id) {
        logger.debug("Fetching trade leg by id: {}", id);
        return tradeLegService.getTradeLegById(id)
                .map(tradeLegMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createTradeLeg(@Valid @RequestBody TradeLegDTO tradeLegDTO) {
        logger.info("Creating new trade leg: {}", tradeLegDTO);
        var entity = tradeLegMapper.toEntity(tradeLegDTO);
        var saved = tradeLegService.saveTradeLeg(entity, tradeLegDTO);
        return ResponseEntity.ok(tradeLegMapper.toDto(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTradeLeg(@PathVariable(name = "id") Long id) {
        logger.warn("Deleting trade leg with id: {}", id);
        tradeLegService.deleteTradeLeg(id);
        return ResponseEntity.noContent().build();
    }
}
