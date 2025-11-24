package com.technicalchallenge.mapper;

import com.technicalchallenge.dto.CashflowDTO;
import com.technicalchallenge.model.Cashflow;

import org.springframework.stereotype.Component;

@Component
public class CashflowMapper {

    public CashflowDTO toDto(Cashflow entity) {

        if (entity == null) {
            return null;
        }

        CashflowDTO dto = new CashflowDTO();
        dto.setId(entity.getId());
        dto.setPaymentValue(entity.getPaymentValue());
        dto.setValueDate(entity.getValueDate());
        dto.setRate(entity.getRate());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setActive(entity.getActive());

        if (entity.getTradeLeg() != null) {
            dto.setLegId(entity.getTradeLeg().getLegId());
        }
        if (entity.getPayRec() != null) {
            dto.setPayRec(entity.getPayRec().getPayRec());
        }
        if (entity.getPaymentType() != null) {
            dto.setPaymentType(entity.getPaymentType().getType());
        }
        if (entity.getPaymentBusinessDayConvention() != null) {
            dto.setPaymentBusinessDayConvention(
                    entity.getPaymentBusinessDayConvention().getBdc());
        }

        return dto;
    }

    public Cashflow toEntity(CashflowDTO dto) {

        Cashflow entity = new Cashflow();
        entity.setId(dto.getId());
        entity.setPaymentValue(dto.getPaymentValue());
        entity.setValueDate(dto.getValueDate());
        entity.setRate(dto.getRate());

        return entity;
    }
}
