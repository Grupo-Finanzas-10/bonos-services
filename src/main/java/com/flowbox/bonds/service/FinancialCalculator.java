package com.flowbox.bonds.service;

import com.flowbox.bonds.model.Bond;
import org.springframework.stereotype.Service;

@Service
public class FinancialCalculator {

    public double calcularTCEA(Bond bond) {
        // Ejemplo simplificado
        double tasa = bond.getCouponRate() / bond.getFrequency();
        return tasa * bond.getFrequency();
    }

    public double calcularTREA(Bond bond) {
        return (bond.getCouponRate() * bond.getNominalValue()) / 1000.0; // ficticio
    }

    public double calcularDuracion(Bond bond) {
        return bond.getMaturityPeriods() / (double) bond.getFrequency();
    }

    public double calcularConvexidad(Bond bond) {
        return 2.0 + bond.getMaturityPeriods() * 0.1;
    }

    public double calcularPrecioMaximo(Bond bond, double tasaOportunidad) {
        double valor = bond.getNominalValue();
        double tasa = tasaOportunidad / bond.getFrequency();
        double precio = 0;
        for (int i = 1; i <= bond.getMaturityPeriods(); i++) {
            precio += (valor * bond.getCouponRate() / bond.getFrequency()) / Math.pow(1 + tasa, i);
        }
        precio += valor / Math.pow(1 + tasa, bond.getMaturityPeriods());
        return precio;
    }
}
