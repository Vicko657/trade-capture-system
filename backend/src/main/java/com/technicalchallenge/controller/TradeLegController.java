package com.technicalchallenge.controller;

import com.technicalchallenge.dto.TradeLegDTO;
import com.technicalchallenge.mapper.TradeLegMapper;
import com.technicalchallenge.service.TradeLegService;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/tradeLegs")
@Validated
@Tag(name = "TradeLeg", description = "Component or part of a larger trade, which represnts one side or segment of a muilt part transcation")
@RequiredArgsConstructor
public class TradeLegController {
    private static final Logger logger = LoggerFactory.getLogger(TradeLegController.class);

    private final TradeLegService tradeLegService;
    private final TradeLegMapper tradeLegMapper;

    @GetMapping
    @PreAuthorize("hasAuthority('READ_TRADE')")
    public List<TradeLegDTO> getAllTradeLegs() {
        logger.info("Fetching all trade legs");
        return tradeLegService.getAllTradeLegs().stream()
                .map(tradeLegMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('READ_TRADE')")
    public ResponseEntity<TradeLegDTO> getTradeLegById(@PathVariable(name = "id") Long id) {
        logger.debug("Fetching trade leg by id: {}", id);
        return tradeLegService.getTradeLegById(id)
                .map(tradeLegMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_TRADE')")
    public ResponseEntity<?> createTradeLeg(@Valid @RequestBody TradeLegDTO tradeLegDTO) {
        logger.info("Creating new trade leg: {}", tradeLegDTO);
        var entity = tradeLegMapper.toEntity(tradeLegDTO);
        var saved = tradeLegService.saveTradeLeg(entity, tradeLegDTO);
        return ResponseEntity.ok(tradeLegMapper.toDto(saved));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('TERMINATE_TRADE')")
    public ResponseEntity<Void> deleteTradeLeg(@PathVariable(name = "id") Long id) {
        logger.warn("Deleting trade leg with id: {}", id);
        tradeLegService.deleteTradeLeg(id);
        return ResponseEntity.noContent().build();
    }
}
