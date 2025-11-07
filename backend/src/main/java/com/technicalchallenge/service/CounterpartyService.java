package com.technicalchallenge.service;

import com.technicalchallenge.exceptions.EntityNotFoundException;
import com.technicalchallenge.exceptions.InActiveException;
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

    public void validateCounterparty(Long id, String name) {
        Counterparty counterparty = null;

        if (id != null && name != null) {

            if (getCounterpartyById(id).isEmpty()) {
                throw new EntityNotFoundException("Counterparty not found by id");
            } else if (getCounterpartyByName(name).isEmpty()) {
                throw new EntityNotFoundException("Counterparty not found by name");
            }
            counterparty = getCounterpartyById(id).get();

            if (!counterparty.isActive()) {
                throw new InActiveException("Counterparty must be active");
            }

        }

    }

    public Counterparty saveCounterparty(Counterparty counterparty) {
        return counterpartyRepository.save(counterparty);
    }

    public void deleteCounterparty(Long id) {
        counterpartyRepository.deleteById(id);
    }
}
