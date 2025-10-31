package com.technicalchallenge.service;

import com.technicalchallenge.exceptions.EntityNotFoundException;
import com.technicalchallenge.model.Index;
import com.technicalchallenge.repository.IndexRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class IndexService {
    private static final Logger logger = LoggerFactory.getLogger(IndexService.class);

    @Autowired
    private IndexRepository indexRepository;

    public List<Index> findAll() {
        logger.info("Retrieving all indexes");
        return indexRepository.findAll();
    }

    public Optional<Index> findById(Long id) {
        logger.debug("Retrieving index by id: {}", id);
        return indexRepository.findById(id);
    }

    public Optional<Index> findByIndex(String index) {
        logger.debug("Retrieving index by index: {}", index);
        return indexRepository.findByIndex(index);
    }

    public void validateIndex(Long id, String index) {

        if (id != null && index != null) {

            if (findById(id).isEmpty()) {
                throw new EntityNotFoundException("Index not found by id");
            } else if (findByIndex(index).isEmpty()) {
                throw new EntityNotFoundException("Index not found by index");
            }
        }

    }

    public Index save(Index index) {
        logger.info("Saving index: {}", index);
        return indexRepository.save(index);
    }

    public void deleteById(Long id) {
        logger.warn("Deleting index with id: {}", id);
        indexRepository.deleteById(id);
    }
}
