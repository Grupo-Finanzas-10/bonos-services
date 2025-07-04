package com.flowbox.bonds.service;

import com.flowbox.bonds.model.Bond;
import com.flowbox.bonds.repository.BondRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BondService {
    private final BondRepository bondRepository;

    public List<Bond> findAll() {
        return bondRepository.findAll();
    }

    public Optional<Bond> findById(Long id) {
        return bondRepository.findById(id);
    }

    public Bond save(Bond bond) {
        return bondRepository.save(bond);
    }

    public void delete(Long id) {
        bondRepository.deleteById(id);
    }
}
