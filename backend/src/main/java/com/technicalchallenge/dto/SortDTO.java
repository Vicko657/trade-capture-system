package com.technicalchallenge.dto;

import org.springframework.web.bind.annotation.RequestParam;

public record SortDTO(@RequestParam(required = false) String sortBy,
        @RequestParam(required = false) String sortDir) {

}
