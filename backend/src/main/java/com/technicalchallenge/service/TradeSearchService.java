package com.technicalchallenge.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.technicalchallenge.dto.PaginationDTO;
import com.technicalchallenge.dto.SearchTradeByCriteria;
import com.technicalchallenge.dto.SortDTO;
import com.technicalchallenge.dto.TradeDTO;
import com.technicalchallenge.mapper.TradeMapper;
import com.technicalchallenge.model.Trade;
import com.technicalchallenge.repository.TradeRepository;
import com.technicalchallenge.specification.TradeSpecification;
import com.technicalchallenge.validation.TradeValidator;

import io.github.perplexhub.rsql.RSQLJPASupport;
import jakarta.transaction.Transactional;

/**
 * TradeSearch service class provides search/filter logic for finding trades.
 */
@Service
@Transactional
public class TradeSearchService {
    private static final Logger logger = LoggerFactory.getLogger(TradeSearchService.class);

    @Autowired
    private TradeValidator tradeValidator;

    @Autowired
    private TradeMapper tradeMapper;

    @Autowired
    private TradeRepository tradeRepository;

    /**
     * Trade: Multi Criteria Search
     * 
     * <p>
     * By counterparty, book, trader, status, date ranges
     * </p>
     * 
     * 
     * FUTURE: Refactor to a much simplier and quicker search using aggregated
     * queries, wasn't initially sure of how complex the search should've been.
     * 
     * @param SearchTradeByCriteria trade unique identifier
     * @return search results
     */
    public List<TradeDTO> getAllTradesByCriteria(SearchTradeByCriteria searchTradeByCriteria) {

        tradeValidator.validateSearch(searchTradeByCriteria);
        logger.debug("Search validation passed to find trade");

        Specification<Trade> specification = TradeSpecification.getTradeCriteria(searchTradeByCriteria);
        logger.info("Retrieving all trades by criteria: {}", searchTradeByCriteria);

        return tradeRepository.findAll(specification).stream()
                .map(tradeMapper::toDto)
                .toList();
    }

    /**
     * Trade: Filtered Search
     * 
     * <p>
     * Multi Criteria By counterparty, book, trader, status, date
     * ranges, paginated filtering and sorting for all trades
     * 
     * - Sorts the trades by column name and in order of ASC or DESC,
     * handles if the sort direction is not filled
     * 
     * - Includes a sorted, unsorted page request for the user to
     * be able to seperately sort or change the pagination
     * 
     * </p>
     * 
     * @param SearchTradeByCriteria JPA Specification Search
     * @param PaginationDTO         Custom pagination
     * @param SortDTO               Custom sort
     * @return returns filtered search results
     */
    public Page<TradeDTO> getAllTrades(SearchTradeByCriteria searchTradeByCriteria, PaginationDTO pagination,
            SortDTO sortFields) {

        tradeValidator.validateSearch(searchTradeByCriteria);

        logger.debug("Search validation passed to find trade");

        // Sort By
        String sortColumn;
        String sortDirection = sortFields.sortDir(); // Default is ASC

        if (sortFields.sortBy() == null || sortFields.sortBy().isBlank()) {
            sortColumn = "tradeId"; // Default is tradeID
        } else {
            sortColumn = sortFields.sortBy();
        }

        Sort sort = null;
        if (sortDirection == null) {
            sort = Sort.unsorted();
        } else if (sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name())) {
            sort = Sort.by(sortColumn).ascending();
        } else if (sortDirection.equalsIgnoreCase(Sort.Direction.DESC.name())) {
            sort = Sort.by(sortColumn).descending();
        }

        // Pagination

        Integer pageNo = pagination.pageNo();
        Integer pageSize = pagination.pageSize();
        Pageable pageable;

        if (pagination.pageNo() != null && pageSize != null && pageNo > 0 && sort != null) {

            // Page Request with sort parameters applied (Page details and sort details are
            // not null)

            pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        } else if (sort == null) {

            // Unsorted page request (With the page details, if the sort is null)

            pageable = PageRequest.of(pageNo - 1, pageSize);
        } else {

            // No pagination setup and seperates the filtered criteria search

            pageable = Pageable.unpaged();
        }

        // Filtered multi criteria search

        Specification<Trade> specification = TradeSpecification.getTradeCriteria(searchTradeByCriteria);

        logger.info("Retrieving all trades by criteria: {}", searchTradeByCriteria);

        return tradeRepository.findAll(specification, pageable).map(tradeMapper::toDto);
    }

    /**
     * Trade: RSQL Search
     * 
     * <p>
     * The RSQL plugin automatically builds the JPA specification with
     * less code and provides filtering support for power users.
     * 
     * Used the plugin recommended which saved time:
     * {@link https://github.com/perplexhub/rsql-jpa-specification}
     * </p>
     * 
     * @param query trade unique identifier
     * @return powerful results
     */
    public List<TradeDTO> getAllTradesByRSQL(String query) {

        tradeValidator.validateRSQLSearch(query);
        logger.debug("Query validation passed to find trade");

        Specification<Trade> specfication = RSQLJPASupport.toSpecification(query);
        logger.info("Retrieving all trades by rsql: {}", query);

        return tradeRepository.findAll(specfication).stream()
                .map(tradeMapper::toDto)
                .toList();
    }

}
