package com.technicalchallenge.mapper;

import com.technicalchallenge.dto.TradeDTO;
import com.technicalchallenge.dto.TradeLegDTO;
import com.technicalchallenge.model.Trade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TradeMapper {

    @Autowired
    private TradeLegMapper tradeLegMapper;

    public TradeDTO toDto(Trade trade) {
        if (trade == null) {
            return null;
        }

        TradeDTO dto = new TradeDTO();
        dto.setId(trade.getId());
        dto.setTradeId(trade.getTradeId());
        dto.setVersion(trade.getVersion());
        dto.setTradeDate(trade.getTradeDate());
        dto.setTradeStartDate(trade.getTradeStartDate());
        dto.setTradeMaturityDate(trade.getTradeMaturityDate());
        dto.setTradeExecutionDate(trade.getTradeExecutionDate());
        dto.setUtiCode(trade.getUtiCode());
        dto.setLastTouchTimestamp(trade.getLastTouchTimestamp());
        dto.setValidityStartDate(trade.getValidityStartDate());
        dto.setValidityEndDate(trade.getValidityEndDate());
        dto.setActive(trade.getActive());
        dto.setCreatedDate(trade.getCreatedDate());

        if (trade.getBook() != null) {
            dto.setBookId(trade.getBook().getId());
            dto.setBookName(trade.getBook().getBookName());
        }

        if (trade.getCounterparty() != null) {
            dto.setCounterpartyId(trade.getCounterparty().getId());
            dto.setCounterpartyName(trade.getCounterparty().getName());
        }

        if (trade.getTraderUser() != null) {
            dto.setTraderUserId(trade.getTraderUser().getId());
            dto.setTraderUserName(trade.getTraderUser().getFirstName() + " " + trade.getTraderUser().getLastName());
        }

        if (trade.getTradeInputterUser() != null) { // Fixed field name
            dto.setTradeInputterUserId(trade.getTradeInputterUser().getId());
            dto.setInputterUserName(
                    trade.getTradeInputterUser().getFirstName() + " " + trade.getTradeInputterUser().getLastName());
        }

        if (trade.getTradeType() != null) {
            dto.setTradeTypeId(trade.getTradeType().getId());
            dto.setTradeType(trade.getTradeType().getTradeType());
        }

        if (trade.getTradeSubType() != null) {
            dto.setTradeSubTypeId(trade.getTradeSubType().getId());
            dto.setTradeSubType(trade.getTradeSubType().getTradeSubType());
        }

        if (trade.getTradeStatus() != null) {
            dto.setTradeStatusId(trade.getTradeStatus().getId());
            dto.setTradeStatus(trade.getTradeStatus().getTradeStatus());
        }

        // Map trade legs
        if (trade.getTradeLegs() != null) {
            List<TradeLegDTO> legDTOs = trade.getTradeLegs().stream()
                    .map(tradeLegMapper::toDto)
                    .collect(Collectors.toList());
            dto.setTradeLegs(legDTOs);
        }

        return dto;
    }

    public Trade toEntity(TradeDTO dto) {
        if (dto == null) {
            return null;
        }

        Trade trade = new Trade();
        trade.setId(dto.getId());
        trade.setTradeId(dto.getTradeId());
        trade.setTradeDate(dto.getTradeDate()); // Fixed field names
        trade.setTradeStartDate(dto.getTradeStartDate());
        trade.setTradeMaturityDate(dto.getTradeMaturityDate());
        trade.setTradeExecutionDate(dto.getTradeExecutionDate());
        trade.setUtiCode(dto.getUtiCode());
        trade.setValidityStartDate(dto.getValidityStartDate());
        return trade;
    }

}
