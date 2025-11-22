package com.technicalchallenge.service;

import com.technicalchallenge.dto.TradeDTO;
import com.technicalchallenge.dto.TradeLegDTO;
import com.technicalchallenge.exceptions.referencedata.TradeLegNotFoundException;
import com.technicalchallenge.model.Trade;
import com.technicalchallenge.model.TradeLeg;
import com.technicalchallenge.repository.TradeLegRepository;
import com.technicalchallenge.validation.TradeValidator;
import com.technicalchallenge.validation.ValidationResult;
import com.technicalchallenge.repository.CurrencyRepository;
import com.technicalchallenge.repository.LegTypeRepository;
import com.technicalchallenge.repository.IndexRepository;
import com.technicalchallenge.repository.HolidayCalendarRepository;
import com.technicalchallenge.repository.ScheduleRepository;
import com.technicalchallenge.repository.BusinessDayConventionRepository;
import com.technicalchallenge.repository.PayRecRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TradeLegService {
    private static final Logger logger = LoggerFactory.getLogger(TradeLegService.class);

    @Autowired
    private TradeLegRepository tradeLegRepository;
    @Autowired
    private CurrencyRepository currencyRepository;
    @Autowired
    private LegTypeRepository legTypeRepository;
    @Autowired
    private IndexRepository indexRepository;
    @Autowired
    private HolidayCalendarRepository holidayCalendarRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private BusinessDayConventionRepository businessDayConventionRepository;
    @Autowired
    private PayRecRepository payRecRepository;
    @Autowired
    private TradeValidator tradeValidator;

    public List<TradeLeg> getAllTradeLegs() {
        logger.info("Retrieving all trade legs");
        return tradeLegRepository.findAll();
    }

    public TradeLeg getTradeLegById(Long id) {
        logger.debug("Retrieving trade leg by id: {}", id);
        return tradeLegRepository.findById(id).orElseThrow(() -> new TradeLegNotFoundException("tradelegId", id));
    }

    public TradeLeg saveTradeLeg(TradeLeg tradeLeg, TradeLegDTO dto) {

        logger.info("Saving trade leg: {}", tradeLeg);
        // Ensure TradeLeg is saved with related entities set, not just IDs

        return tradeLegRepository.save(tradeLeg);
    }

    // Creates TradeLegs: Moved creation method out of TradeService
    public List<TradeLeg> createTradeLegs(TradeDTO tradeDTO, Trade savedTrade) {

        List<TradeLeg> tradeLegs = new ArrayList<>();

        for (int i = 0; i < tradeDTO.getTradeLegs().size(); i++) {

            var legDTO = tradeDTO.getTradeLegs().get(i);

            TradeLeg tradeLeg = new TradeLeg();

            // Validate cross legs rules
            ValidationResult tradeCrossLegValidation = tradeValidator
                    .validateTradeLegConsistency(tradeDTO.getTradeLegs());
            tradeCrossLegValidation.throwifNotValid();

            tradeLeg.setTrade(savedTrade);
            tradeLeg.setNotional(legDTO.getNotional());
            tradeLeg.setRate(legDTO.getRate());
            tradeLeg.setActive(true);
            tradeLeg.setCreatedDate(LocalDateTime.now());

            // Populate reference data for leg
            populateLegReferenceData(tradeLeg, legDTO);

            // Adds the Leg into the list
            tradeLegs.add(tradeLeg);

        }

        return tradeLegs;
    }

    private void populateLegReferenceData(TradeLeg leg, TradeLegDTO legDTO) {
        // Populate currency by name or ID
        if (legDTO.getCurrency() != null) {
            currencyRepository.findByCurrency(legDTO.getCurrency())
                    .ifPresent(leg::setCurrency);
        } else if (legDTO.getCurrencyId() != null) {
            currencyRepository.findById(legDTO.getCurrencyId())
                    .ifPresent(leg::setCurrency);
        }

        // Populate leg type by name or ID
        if (legDTO.getLegType() != null) {
            legTypeRepository.findByType(legDTO.getLegType())
                    .ifPresent(leg::setLegRateType);
        } else if (legDTO.getLegTypeId() != null) {
            legTypeRepository.findById(legDTO.getLegTypeId())
                    .ifPresent(leg::setLegRateType);
        }

        // Populate index by name or ID
        if (legDTO.getIndexName() != null) {
            indexRepository.findByIndex(legDTO.getIndexName())
                    .ifPresent(leg::setIndex);
        } else if (legDTO.getIndexId() != null) {
            indexRepository.findById(legDTO.getIndexId())
                    .ifPresent(leg::setIndex);
        }

        // Populate holiday calendar by name or ID
        if (legDTO.getHolidayCalendar() != null) {
            holidayCalendarRepository.findByHolidayCalendar(legDTO.getHolidayCalendar())
                    .ifPresent(leg::setHolidayCalendar);
        } else if (legDTO.getHolidayCalendarId() != null) {
            holidayCalendarRepository.findById(legDTO.getHolidayCalendarId())
                    .ifPresent(leg::setHolidayCalendar);
        }

        // Populate schedule by name or ID
        if (legDTO.getCalculationPeriodSchedule() != null) {
            scheduleRepository.findBySchedule(legDTO.getCalculationPeriodSchedule())
                    .ifPresent(leg::setCalculationPeriodSchedule);
        } else if (legDTO.getScheduleId() != null) {
            scheduleRepository.findById(legDTO.getScheduleId())
                    .ifPresent(leg::setCalculationPeriodSchedule);
        }

        // Populate payment business day convention by name or ID
        if (legDTO.getPaymentBusinessDayConvention() != null) {
            businessDayConventionRepository.findByBdc(legDTO.getPaymentBusinessDayConvention())
                    .ifPresent(leg::setPaymentBusinessDayConvention);
        } else if (legDTO.getPaymentBdcId() != null) {
            businessDayConventionRepository.findById(legDTO.getPaymentBdcId())
                    .ifPresent(leg::setPaymentBusinessDayConvention);
        }

        // Populate fixing business day convention by name or ID
        if (legDTO.getFixingBusinessDayConvention() != null) {
            businessDayConventionRepository.findByBdc(legDTO.getFixingBusinessDayConvention())
                    .ifPresent(leg::setFixingBusinessDayConvention);
        } else if (legDTO.getFixingBdcId() != null) {
            businessDayConventionRepository.findById(legDTO.getFixingBdcId())
                    .ifPresent(leg::setFixingBusinessDayConvention);
        }

        // Populate pay/receive flag by name or ID
        if (legDTO.getPayReceiveFlag() != null) {
            payRecRepository.findByPayRec(legDTO.getPayReceiveFlag())
                    .ifPresent(leg::setPayReceiveFlag);
        } else if (legDTO.getPayRecId() != null) {
            payRecRepository.findById(legDTO.getPayRecId())
                    .ifPresent(leg::setPayReceiveFlag);
        }
    }

    public void deleteTradeLeg(Long id) {
        logger.warn("Deleting trade leg with id: {}", id);
        tradeLegRepository.deleteById(id);
    }

    // Business logic: notional > 0, trade, currency, legRateType required (enforced
    // in controller)
}
