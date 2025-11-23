package com.technicalchallenge.service;

import com.technicalchallenge.exceptions.referencedata.CurrencyNotFoundException;
import com.technicalchallenge.model.Currency;
import com.technicalchallenge.repository.CurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class CurrencyService {
    private static final Logger logger = LoggerFactory.getLogger(CurrencyService.class);

    @Autowired
    private CurrencyRepository currencyRepository;

    public List<Currency> findAll() {
        logger.info("Retrieving all currencies");
        return currencyRepository.findAll();
    }

    public Optional<Currency> findById(Long id) {
        logger.debug("Retrieving currency by id: {}", id);
        return currencyRepository.findById(id);
    }

    public Optional<Currency> findByCurrency(String currency) {
        logger.debug("Retrieving currency by currency: {}", currency);
        return currencyRepository.findByCurrency(currency);
    }

    public Currency save(Currency currency) {
        logger.info("Saving currency: {}", currency);
        return currencyRepository.save(currency);
    }

    public void deleteById(Long id) {
        logger.warn("Deleting currency with id: {}", id);
        currencyRepository.deleteById(id);
    }

    // Checks the Reference Data for Trade Leg Service
    public Currency findId(Long id) {
        logger.debug("Retrieving currency by id: {}", id);
        return currencyRepository.findById(id).orElseThrow(() -> new CurrencyNotFoundException("currencyId", id));
    }

    public Currency findCurrency(String currency) {
        logger.debug("Retrieving currency by currency: {}", currency);
        return currencyRepository.findByCurrency(currency)
                .orElseThrow(() -> new CurrencyNotFoundException("currency", currency));
    }
}
