package com.technicalchallenge.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.technicalchallenge.dto.PaginationDTO;
import com.technicalchallenge.dto.SearchTradeByCriteria;
import com.technicalchallenge.dto.SortDTO;
import com.technicalchallenge.dto.TradeDTO;
import com.technicalchallenge.service.TradeSearchService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Rest Controller for managing the trade searches.
 * 
 * API endpoints to complete search and filters operations.
 */
@RestController
@RequestMapping("/api/trades")
@Validated
@Tag(name = "Trade Searches", description = "Trade Search management operations searching, pagination and filtering")
public class TradeSearchController {

    // TradeSearchService layer
    @Autowired
    private TradeSearchService tradeSearchService;

    @Operation(summary = "Get all trades by search criteria", description = "Retrieves Trades by counterparty, book, trader, status, date ranges and returns comprehensive trade information including legs and cashflows.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all trades under the searched criteria", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TradeDTO.class))),
            @ApiResponse(responseCode = "204", description = "No Trades found"),
            @ApiResponse(responseCode = "401", description = "User's access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('READ_TRADE')")
    public ResponseEntity<List<TradeDTO>> getAllTradesByCriteria(
            @Valid SearchTradeByCriteria searchTradeByCriteria) {

        List<TradeDTO> trades = tradeSearchService.getAllTradesByCriteria(searchTradeByCriteria);
        return ResponseEntity.ok(trades);

    }

    @Operation(summary = "Get a result of paginated filtered trades by filter", description = "Returns a page of trades, that can be filtered, paginated or sorted")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated filtered trades", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TradeDTO.class))),
            @ApiResponse(responseCode = "204", description = "No Trades found"),
            @ApiResponse(responseCode = "401", description = "User's access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/filter")
    @PreAuthorize("hasAuthority('READ_TRADE')")
    public ResponseEntity<Page<TradeDTO>> getAllTrades(@Valid @RequestParam SearchTradeByCriteria searchTradeByCriteria,
            PaginationDTO pagination, SortDTO sort) {

        Page<TradeDTO> trades = tradeSearchService.getAllTrades(searchTradeByCriteria, pagination, sort);
        return ResponseEntity.ok(trades);
    }

    @Operation(summary = "Get all trades by rsql", description = "Retrieves a list of trades filtered by RSQL JPA Spring Boot starter query plugin, io.github.perplexhub:rsql-jpa-spring-boot-starter, to process dynamic RSQL query strings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all trades by RSQL", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TradeDTO.class))),
            @ApiResponse(responseCode = "204", description = "No Trades found"),
            @ApiResponse(responseCode = "401", description = "User's access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/rsql")
    @PreAuthorize("hasAuthority('READ_TRADE')")
    public ResponseEntity<List<TradeDTO>> getTradesByRSQL(
            @Valid @RequestParam(value = "query", required = false) String query) {

        List<TradeDTO> trades = tradeSearchService.getAllTradesByRSQL(query);
        return ResponseEntity.ok(trades);

    }

}
