package com.technicalchallenge.dto;

import org.springframework.web.bind.annotation.RequestParam;

public record PaginationDTO(

                @RequestParam(required = false) Integer pageNo,
                @RequestParam(required = false) Integer pageSize) {

}
