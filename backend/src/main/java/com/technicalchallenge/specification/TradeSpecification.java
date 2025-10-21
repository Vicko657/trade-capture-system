package com.technicalchallenge.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.technicalchallenge.dto.SearchTradeByCriteria;
import com.technicalchallenge.model.ApplicationUser;
import com.technicalchallenge.model.Book;
import com.technicalchallenge.model.Counterparty;
import com.technicalchallenge.model.Trade;
import com.technicalchallenge.model.TradeStatus;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;

public class TradeSpecification {

    // Using JPA Specifications to abstract the Criteria API to create a Multi
    // Search Criteria
    public static Specification<Trade> getTradeCriteria(SearchTradeByCriteria searchTradeByCriteria) {

        // Array of Predicates
        List<Predicate> predicates = new ArrayList<>();

        return (root, query, criteriaBuilder) -> {

            // Book Predicate - Many to One Relationship (JOIN: Book)
            if (searchTradeByCriteria.bookName() != null) {
                Join<Trade, Book> tradeBook = root.join("book");
                // Searches bookName
                Predicate bookNamePredicate = criteriaBuilder
                        .like(tradeBook.get("bookName"), searchTradeByCriteria.bookName());
                predicates.add(bookNamePredicate);
            }

            // Counterparty Predicate - Many to One Relationship (JOIN: Counterparty)
            if (searchTradeByCriteria.counterpartyName() != null) {
                Join<Trade, Counterparty> tradeCounterparty = root.join("counterparty");
                // Searches counterpartyName
                Predicate counterpartyNamePredicate = criteriaBuilder
                        .like(tradeCounterparty.get("name"),
                                searchTradeByCriteria.counterpartyName());
                predicates.add(counterpartyNamePredicate);
            }

            // Trader Predicate - Many to One Relationship (JOIN:
            // ApplicationUser.traderUser) - Seperated FirstName and LastName
            if (searchTradeByCriteria.traderUserFirstName() != null) {
                Join<Trade, ApplicationUser> tradeTraderUser = root.join("traderUser");
                // Searches traderUserFirstName
                Predicate traderUserNamePredicate = criteriaBuilder
                        .like(tradeTraderUser.get("firstName"),
                                searchTradeByCriteria.traderUserFirstName());
                predicates.add(traderUserNamePredicate);
            }

            if (searchTradeByCriteria.traderUserLastName() != null) {
                Join<Trade, ApplicationUser> tradeTraderUser = root.join("traderUser");
                // Searches traderUserLastName
                Predicate traderUserNamePredicate = criteriaBuilder
                        .like(tradeTraderUser.get("lastName"),
                                searchTradeByCriteria.traderUserLastName());
                predicates.add(traderUserNamePredicate);
            }

            // TraderInputter Predicate - Many to One Relationship (JOIN:
            // ApplicationUser.tradeInputterUser) - Seperated FirstName and LastName
            if (searchTradeByCriteria.inputterUserFirstName() != null) {
                Join<Trade, ApplicationUser> tradeTraderInputter = root.join("tradeInputterUser");
                // Searches traderInputterUserFirstName
                Predicate tradeTraderInputterPredicate = criteriaBuilder
                        .like(tradeTraderInputter.get("firstName"),
                                searchTradeByCriteria.inputterUserFirstName());
                predicates.add(tradeTraderInputterPredicate);
            }
            if (searchTradeByCriteria.inputterUserLastName() != null) {
                Join<Trade, ApplicationUser> tradeTraderInputter = root.join("tradeInputterUser");
                // Searches traderInputterUserLastName
                Predicate traderTraderInputterPredicate = criteriaBuilder
                        .like(tradeTraderInputter.get("lastName"),
                                searchTradeByCriteria.inputterUserLastName());
                predicates.add(traderTraderInputterPredicate);
            }

            // TradeStatus Predicate - Many to One Relationship (JOIN: TradeStatus)
            if (searchTradeByCriteria.tradeStatus() != null) {
                Join<Trade, TradeStatus> tradeStatus = root.join("tradeStatus");
                // Searches traderStatus
                Predicate tradeStatusPredicate = criteriaBuilder
                        .like(tradeStatus.get("tradeStatus"), searchTradeByCriteria.tradeStatus());
                predicates.add(tradeStatusPredicate);
            }

            // TradeDate Predicate - Date Range (BETWEEN: Any Trades Between StartDate and
            // EndDate)
            if (searchTradeByCriteria.tradeStartDate() != null
                    && searchTradeByCriteria.tradeEndDate() != null) {
                // Searches traderDate with seleted start and end dates
                Predicate tradeDatePredicate = criteriaBuilder
                        .between(root.get("tradeDate"), searchTradeByCriteria.tradeStartDate(),
                                searchTradeByCriteria.tradeEndDate());
                predicates.add(tradeDatePredicate);
            }

            // Combines each predicate into an array, they can be searched seperately or
            // together
            return criteriaBuilder.and(predicates.toArray(predicates.toArray(new Predicate[0])));

        };
    }

}
