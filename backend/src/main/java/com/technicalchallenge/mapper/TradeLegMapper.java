package com.technicalchallenge.mapper;

import com.technicalchallenge.dto.CashflowDTO;
import com.technicalchallenge.dto.TradeLegDTO;
import com.technicalchallenge.model.TradeLeg;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TradeLegMapper {

    @Autowired
    private CashflowMapper cashflowMapper;

    public TradeLegDTO toDto(TradeLeg leg) {

        if (leg == null) {
            return null;
        }

        TradeLegDTO dto = new TradeLegDTO();
        dto.setLegId(leg.getLegId());
        dto.setNotional(leg.getNotional());
        dto.setRate(leg.getRate());

        if (leg.getCurrency() != null) {
            dto.setCurrencyId(leg.getCurrency().getId());
            dto.setCurrency(leg.getCurrency().getCurrency());
        }

        if (leg.getLegRateType() != null) {
            dto.setLegTypeId(leg.getLegRateType().getId());
            dto.setLegType(leg.getLegRateType().getType());
        }

        if (leg.getIndex() != null) {
            dto.setIndexId(leg.getIndex().getId());
            dto.setIndexName(leg.getIndex().getIndex()); // Fixed: setIndex() -> setIndexName()
        }

        if (leg.getHolidayCalendar() != null) {
            dto.setHolidayCalendarId(leg.getHolidayCalendar().getId());
            dto.setHolidayCalendar(leg.getHolidayCalendar().getHolidayCalendar());
        }

        if (leg.getCalculationPeriodSchedule() != null) {
            dto.setScheduleId(leg.getCalculationPeriodSchedule().getId());
            dto.setCalculationPeriodSchedule(leg.getCalculationPeriodSchedule().getSchedule());
        }

        if (leg.getPaymentBusinessDayConvention() != null) {
            dto.setPaymentBdcId(leg.getPaymentBusinessDayConvention().getId());
            dto.setPaymentBusinessDayConvention(leg.getPaymentBusinessDayConvention().getBdc());
        }

        if (leg.getFixingBusinessDayConvention() != null) {
            dto.setFixingBdcId(leg.getFixingBusinessDayConvention().getId());
            dto.setFixingBusinessDayConvention(leg.getFixingBusinessDayConvention().getBdc());
        }

        if (leg.getPayReceiveFlag() != null) {
            dto.setPayRecId(leg.getPayReceiveFlag().getId());
            dto.setPayReceiveFlag(leg.getPayReceiveFlag().getPayRec());
        }

        // Map cashflows
        if (leg.getCashflows() != null) {
            List<CashflowDTO> cashflowDTOs = leg.getCashflows().stream()
                    .map(cashflowMapper::toDto)
                    .collect(Collectors.toList());
            dto.setCashflows(cashflowDTOs);
        }

        return dto;
    }

    public TradeLeg toEntity(TradeLegDTO dto) {

        if (dto == null) {
            return null;
        }

        TradeLeg entity = new TradeLeg();
        entity.setLegId(dto.getLegId());
        entity.setNotional(dto.getNotional());
        entity.setRate(dto.getRate());

        return entity;
    }
}
