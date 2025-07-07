package com.flowbox.bonds.dto;

import com.flowbox.bonds.service.FinancialCalculator;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlujoCajaResponse {
    private int periodo;
    private double interes;
    private double principal;
    private double flujoTotal;
    private String descripcion;
    
    public static FlujoCajaResponse fromFlujoCaja(FinancialCalculator.FlujoCaja flujoCaja) {
        return FlujoCajaResponse.builder()
                .periodo(flujoCaja.getPeriodo())
                .interes(flujoCaja.getInteres())
                .principal(flujoCaja.getPrincipal())
                .flujoTotal(flujoCaja.getFlujoTotal())
                .descripcion(flujoCaja.getDescripcion())
                .build();
    }
} 