package com.technicalchallenge.repository;

import com.technicalchallenge.dto.DailySummaryDTO;
import com.technicalchallenge.dto.TradeSummaryDTO;
import com.technicalchallenge.model.Trade;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

// Trade Repository extends a JPASpecificationExecutor<Trade> to handle multi criteria search
@Repository
public interface TradeRepository extends JpaRepository<Trade, Long>, JpaSpecificationExecutor<Trade> {

    // Existing methods
    List<Trade> findByTradeId(Long tradeId);

    @Query("SELECT MAX(t.tradeId) FROM Trade t")
    Optional<Long> findMaxTradeId();

    @Query("SELECT MAX(t.version) FROM Trade t WHERE t.tradeId = :tradeId")
    Optional<Integer> findMaxVersionByTradeId(@Param("tradeId") Long tradeId);

    // NEW METHODS for service layer compatibility
    Optional<Trade> findByTradeIdAndActiveTrue(Long tradeId);

    List<Trade> findByActiveTrueOrderByTradeIdDesc();

    @Query("SELECT t FROM Trade t WHERE t.tradeId = :tradeId AND t.active = true ORDER BY t.version DESC")
    Optional<Trade> findLatestActiveVersionByTradeId(@Param("tradeId") Long tradeId);

    // Paginated Filtering
    Page<Trade> findAll(Specification<Trade> specfication, Pageable pageable);

    // Trader Dashboard and Blotters Summaries

    // Trade Summary DTO

    // Trader's personal trades
    @Query("SELECT new com.technicalchallenge.dto.TradeSummaryDTO$PersonalView(CONCAT(t.traderUser.firstName,' ', t.traderUser.lastName), t.tradeId, t.tradeDate, t.tradeExecutionDate, t.tradeType.tradeType, t.utiCode, t.tradeStatus.tradeStatus, t.book.bookName, t.counterparty.name, t.version) FROM Trade t JOIN t.traderUser u WHERE t.traderUser.loginId = :username AND t.active = true GROUP BY t.traderUser.loginId ORDER BY t.tradeType ASC")
    Page<TradeSummaryDTO.PersonalView> findPersonalTradesView(@Param("username") String username,
            Pageable pageable);

    // Total results
    @Query("SELECT COUNT(t.tradeId), COALESCE(SUM(l.notional), 0) FROM Trade t JOIN t.tradeLegs l JOIN t.traderUser u WHERE t.traderUser.loginId = :username")
    Object findResultsOfTotals(@Param("username") String username);

}
