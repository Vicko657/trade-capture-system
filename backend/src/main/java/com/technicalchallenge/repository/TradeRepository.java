package com.technicalchallenge.repository;

import com.technicalchallenge.dto.DailySummaryDTO.BookActivity;
import com.technicalchallenge.dto.TradeSummaryDTO.CounterpartyBreakdown;
import com.technicalchallenge.dto.TradeSummaryDTO.PersonalView;
import com.technicalchallenge.dto.TradeSummaryDTO.RiskExposure;
import com.technicalchallenge.dto.TradeSummaryDTO.TradeTypeBreakdown;
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
    Page<PersonalView> findPersonalTradesView(@Param("username") String username,
            Pageable pageable);

    // Total results
    @Query("SELECT COUNT(t.tradeId), COALESCE(SUM(l.notional), 0) FROM Trade t JOIN t.tradeLegs l JOIN t.traderUser u WHERE t.traderUser.loginId = :username")
    Object findResultsOfTotals(@Param("username") String username);

    // Total number of trades by status
    @Query("SELECT t FROM Trade t JOIN t.traderUser u WHERE traderUser.loginId = :username")
    List<Trade> findAllTrades(@Param("username") String username);

    // Breakdown by trade type
    @Query("SELECT new com.technicalchallenge.dto.TradeSummaryDTO$TradeTypeBreakdown(t.tradeType.tradeType, SUM(l.notional), ROUND(SUM(l.notional) / SUM(SUM(l.notional)) OVER() * 100, 2)) FROM Trade t JOIN t.tradeLegs l JOIN t.traderUser u WHERE t.traderUser.loginId = :username GROUP BY t.tradeType")
    List<TradeTypeBreakdown> findByTradeTypeBreakdown(@Param("username") String username);

    // Breakdown by counterparty
    @Query("SELECT new com.technicalchallenge.dto.TradeSummaryDTO$CounterpartyBreakdown(t.counterparty.name, SUM(l.notional), ROUND(SUM(l.notional) / SUM(SUM(l.notional)) OVER() * 100, 2)) FROM Trade t JOIN t.counterparty c JOIN t.tradeLegs l JOIN t.traderUser u WHERE t.traderUser.loginId = :username GROUP BY c.name")
    List<CounterpartyBreakdown> findByCounterpartyBreakdown(@Param("username") String username);

    // Risk Exposure by pay/rec
    @Query("SELECT new com.technicalchallenge.dto.TradeSummaryDTO$RiskExposure(l.legId, l.rate, sd.desk.deskName, l.currency.currency, l.payReceiveFlag.payRec, SUM(CASE WHEN l.payReceiveFlag.payRec = 'Receive' THEN l.notional ELSE -l.notional END)) FROM Trade t JOIN t.book b JOIN b.costCenter cc JOIN cc.subDesk sd JOIN t.tradeLegs l JOIN t.traderUser u WHERE t.traderUser.loginId = :username GROUP BY l.payReceiveFlag.payRec")
    List<RiskExposure> findRiskExposure(@Param("username") String username);

    // Daily Summary DTO

    // Book Level Activity
    @Query("SELECT new com.technicalchallenge.dto.DailySummaryDTO$BookActivity(b.bookName, b.costCenter.costCenterName, cc.subDesk.subdeskName, COALESCE(SUM(l.notional), 0) ,b.version) FROM Trade t JOIN t.book b JOIN b.costCenter cc JOIN cc.subDesk sd  JOIN t.tradeLegs l JOIN t.traderUser u WHERE t.traderUser.loginId = :username AND t.book.id = :bookId GROUP BY b.bookName, b.version, b.costCenter.costCenterName ORDER BY b.version")
    List<BookActivity> findBookLevelActivitySummary(@Param("username") String username, @Param("bookId") Long bookId);

}
