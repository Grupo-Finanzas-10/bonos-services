package com.flowbox.bonds.dto;

import com.flowbox.bonds.model.Bond;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimulationResponse {
    
    // Cálculos financieros
    private double tcea;
    private double trea;
    private double duracion;
    private double convexidad;
    private double precioMaximo;
    private double duracionModificada;
    
    // Información del bono
    private BondInfo bondInfo;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BondInfo {
        private String nombre;
        private double valorNominal;
        private double tasaCupon;
        private int periodosVencimiento;
        private int frecuencia;
        private double tasaMercado;
        private int periodosGracia;
        private String tipoGracia;
        private int capitalizacion;
        private String moneda;
        private String tipoInteres;
    }
    
    public static SimulationResponse fromBond(Bond bond, double tcea, double trea, 
                                            double duracion, double convexidad, 
                                            double precioMaximo, double duracionModificada) {
        
        BondInfo bondInfo = BondInfo.builder()
                .nombre(bond.getName())
                .valorNominal(bond.getNominalValue())
                .tasaCupon(bond.getCouponRate())
                .periodosVencimiento(bond.getMaturityPeriods())
                .frecuencia(bond.getFrequency())
                .tasaMercado(bond.getMarketRate())
                .periodosGracia(bond.getGracePeriods())
                .tipoGracia(bond.getGraceType())
                .capitalizacion(bond.getCapitalization())
                .moneda(bond.getCurrency())
                .tipoInteres(bond.getInterestType())
                .build();
        
        return SimulationResponse.builder()
                .tcea(tcea)
                .trea(trea)
                .duracion(duracion)
                .convexidad(convexidad)
                .precioMaximo(precioMaximo)
                .duracionModificada(duracionModificada)
                .bondInfo(bondInfo)
                .build();
    }
} 