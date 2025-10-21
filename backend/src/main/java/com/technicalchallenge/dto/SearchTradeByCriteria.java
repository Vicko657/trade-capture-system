package com.technicalchallenge.dto;

import java.time.LocalDate;

import org.springframework.web.bind.annotation.RequestParam;

// Created a DTO that has the Search parameters - all are not required to search
public record SearchTradeByCriteria(
                @RequestParam(required = false) String bookName,
                @RequestParam(required = false) String counterpartyName,
                @RequestParam(required = false) String traderUserFirstName,
                @RequestParam(required = false) String traderUserLastName,
                @RequestParam(required = false) String inputterUserFirstName,
                @RequestParam(required = false) String inputterUserLastName,
                @RequestParam(required = false) String tradeStatus,
                @RequestParam(required = false) LocalDate tradeStartDate,
                @RequestParam(required = false) LocalDate tradeEndDate) {

}
