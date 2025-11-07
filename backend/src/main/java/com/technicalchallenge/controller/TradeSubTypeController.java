package com.technicalchallenge.controller;

import com.technicalchallenge.model.TradeSubType;
import com.technicalchallenge.repository.TradeSubTypeRepository;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tradeSubTypes")
@Tag(name = "TradeSubType", description = "Specific categories of a trade, that define its characteristics")
public class TradeSubTypeController {
    @Autowired
    private TradeSubTypeRepository tradeSubTypeRepository;

    @GetMapping("/values")
    public List<String> getTradeSubTypeValues() {
        List<TradeSubType> subTypes = tradeSubTypeRepository.findAll();
        return subTypes.stream().map(TradeSubType::getTradeSubType).collect(Collectors.toList());
    }
}
