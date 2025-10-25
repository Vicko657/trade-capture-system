package com.technicalchallenge.dto;

import org.springframework.web.bind.annotation.RequestParam;

public record SortDTO(@RequestParam(required = false, defaultValue = "tradeId") String sortBy,
        @RequestParam(required = false, defaultValue = "ASC") String sortDir) {

}
