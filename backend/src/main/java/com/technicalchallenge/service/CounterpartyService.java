package com.technicalchallenge.service;

import com.technicalchallenge.exceptions.referencedata.CounterpartyNotFoundException;
import com.technicalchallenge.model.Counterparty;
import com.technicalchallenge.repository.CounterpartyRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CounterpartyService {

    @Autowired
    private CounterpartyRepository counterpartyRepository;

    public List<Counterparty> getAllCounterparties() {
        return counterpartyRepository.findAll();
    }

    public Optional<Counterparty> getCounterpartyById(Long id) {
        return counterpartyRepository.findById(id);
    }

    public Optional<Counterparty> getCounterpartyByName(String name) {
        return counterpartyRepository.findByName(name);
    }

    public Counterparty saveCounterparty(Counterparty counterparty) {
        return counterpartyRepository.save(counterparty);
    }

    public void deleteCounterparty(Long id) {
        counterpartyRepository.deleteById(id);
    }

    // Checks the Reference Data for Trade Service
    public Counterparty findCounterpartyId(Long id) {
        return counterpartyRepository.findById(id)
                .orElseThrow(() -> new CounterpartyNotFoundException("counterpartyId", id));
    }

    public Counterparty findCounterpartyName(String counterpartyName) {
        return counterpartyRepository.findByName(counterpartyName)
                .orElseThrow(() -> new CounterpartyNotFoundException("counterpartyName", counterpartyName));
    }
}
