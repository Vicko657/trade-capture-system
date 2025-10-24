package com.technicalchallenge.dto;

import org.springframework.web.bind.annotation.RequestParam;

public record PaginationDTO(

        @RequestParam(required = false, defaultValue = "1") int pageNo,
        @RequestParam(required = false, defaultValue = "1") int pageSize) {

}
