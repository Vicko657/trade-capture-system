package com.technicalchallenge.service;

import com.technicalchallenge.dto.TradeDTO;
import com.technicalchallenge.dto.TradeLegDTO;
import com.technicalchallenge.mapper.TradeLegMapper;
import com.technicalchallenge.model.BusinessDayConvention;
import com.technicalchallenge.model.Currency;
import com.technicalchallenge.model.HolidayCalendar;
import com.technicalchallenge.model.Index;
import com.technicalchallenge.model.LegType;
import com.technicalchallenge.model.PayRec;
import com.technicalchallenge.model.Schedule;
import com.technicalchallenge.model.Trade;
import com.technicalchallenge.model.TradeLeg;
import com.technicalchallenge.repository.TradeLegRepository;
import com.technicalchallenge.validation.ReferenceDataValidator;
import com.technicalchallenge.validation.TradeValidator;
import com.technicalchallenge.validation.ValidationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TradeLegService {
    private static final Logger logger = LoggerFactory.getLogger(TradeLegService.class);

    @Autowired
    private TradeLegRepository tradeLegRepository;
    @Autowired
    private TradeValidator tradeValidator;
    @Autowired
    private ReferenceDataValidator referenceDataValidator;
    @Autowired
    private TradeLegMapper tradeLegMapper;

    public List<TradeLeg> getAllTradeLegs() {
        logger.info("Retrieving all trade legs");
        return tradeLegRepository.findAll();
    }

    public Optional<TradeLeg> getTradeLegById(Long id) {
        logger.debug("Retrieving trade leg by id: {}", id);
        return tradeLegRepository.findById(id);
    }

    public TradeLeg saveTradeLeg(TradeLeg tradeLeg, TradeLegDTO dto) {

        logger.info("Saving trade leg: {}", tradeLeg);
        // Ensure TradeLeg is saved with related entities set, not just IDs

        return tradeLegRepository.save(tradeLeg);
    }

    // Creates TradeLegs: Moved creation method out of TradeService
    public List<TradeLeg> createTradeLegs(TradeDTO tradeDTO, Trade savedTrade) {

        // Validate cross legs rules
        ValidationResult tradeCrossLegValidation = tradeValidator
                .validateTradeLegConsistency(tradeDTO.getTradeLegs());
        tradeCrossLegValidation.throwifNotValid();

        List<TradeLeg> tradeLegs = new ArrayList<>();

        for (int i = 0; i < tradeDTO.getTradeLegs().size(); i++) {

            var legDTO = tradeDTO.getTradeLegs().get(i);

            TradeLeg tradeLeg = tradeLegMapper.toEntity(legDTO);
            tradeLeg.setTrade(savedTrade);
            tradeLeg.setActive(true);
            tradeLeg.setCreatedDate(LocalDateTime.now());

            // Populate reference data for leg
            populateLegReferenceData(tradeLeg, legDTO);

            TradeLeg savedLeg = tradeLegRepository.save(tradeLeg);

            // Adds the Leg into the list
            tradeLegs.add(savedLeg);

        }

        return tradeLegs;
    }

    // UPDATED: Using the ReferenceDataValidator instead of repositories to reduce
    // code repetition
    private void populateLegReferenceData(TradeLeg leg, TradeLegDTO legDTO) {

        // Populate currency by name or ID
        Currency currency = referenceDataValidator.validateCurrencyReference(legDTO);
        leg.setCurrency(currency);

        // Populate leg type by name or ID
        LegType legType = referenceDataValidator.validateLegTypeReference(legDTO);
        leg.setLegRateType(legType);

        // Populate index by name or ID
        Index index = referenceDataValidator.validateIndexReference(legDTO);
        leg.setIndex(index);

        // Populate holiday calendar by name or ID
        HolidayCalendar holidayCalendar = referenceDataValidator.validateHolidayCalendarReference(legDTO);
        leg.setHolidayCalendar(holidayCalendar);

        // Populate schedule by name or ID
        Schedule calculationPerioSchedule = referenceDataValidator.validateScheduleReference(legDTO);
        leg.setCalculationPeriodSchedule(calculationPerioSchedule);

        // Populate payment business day convention by name or ID
        BusinessDayConvention paymentBusinessDayConvention = referenceDataValidator
                .validatePaymentBusinessDayConventionReference(legDTO);
        leg.setPaymentBusinessDayConvention(paymentBusinessDayConvention);

        // Populate fixing business day convention by name or ID
        BusinessDayConvention fixingBusinessDayConvention = referenceDataValidator
                .validateFixingBusinessDayConventionReference(legDTO);
        leg.setFixingBusinessDayConvention(fixingBusinessDayConvention);

        // Populate pay/receive flag by name or ID
        PayRec payRec = referenceDataValidator.validatePayRecReference(legDTO);
        leg.setPayReceiveFlag(payRec);
    }

    public void deleteTradeLeg(Long id) {
        logger.warn("Deleting trade leg with id: {}", id);
        tradeLegRepository.deleteById(id);
    }

    // Business logic: notional > 0, trade, currency, legRateType required (enforced
    // in controller)
}
