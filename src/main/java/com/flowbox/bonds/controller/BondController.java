package com.flowbox.bonds.controller;

import com.flowbox.bonds.model.Bond;
import com.flowbox.bonds.service.BondService;
import com.flowbox.bonds.service.FinancialCalculator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/bonds")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class BondController {

    private final BondService bondService;
    private final FinancialCalculator calculator;

    @GetMapping
    public List<Bond> getAll() {
        return bondService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Bond> getById(@PathVariable Long id) {
        return bondService.findById(id);
    }

    @PostMapping
    public Bond create(@Valid @RequestBody Bond bond) {
        bond.setCreatedAt(LocalDate.now());
        return bondService.save(bond);
    }

    @PutMapping("/{id}")
    public Bond update(@PathVariable Long id, @Valid @RequestBody Bond bond) {
        bond.setId(id);
        return bondService.save(bond);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        bondService.delete(id);
    }

    @PostMapping("/simulate")
    public Map<String, Double> simulate(@RequestBody Bond bond, @RequestParam double tasaOportunidad) {
        Map<String, Double> result = new HashMap<>();
        result.put("TCEA", calculator.calcularTCEA(bond));
        result.put("TREA", calculator.calcularTREA(bond));
        result.put("Duracion", calculator.calcularDuracion(bond));
        result.put("Convexidad", calculator.calcularConvexidad(bond));
        result.put("PrecioMaximo", calculator.calcularPrecioMaximo(bond, tasaOportunidad));
        return result;
    }
}
