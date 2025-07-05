package com.flowbox.bonds.dto;

import com.flowbox.bonds.model.Bond;
import com.flowbox.bonds.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class BondWithUserResponse {
    private Long id;
    private String name;
    private double nominalValue;
    private double couponRate;
    private int maturityPeriods;
    private int frequency;
    private double marketRate;
    private int gracePeriods;
    private String graceType;
    private String currency;
    private String interestType;
    private int capitalization;
    private LocalDate createdAt;
    
    // ID del usuario
    private Long userId;

    public static BondWithUserResponse fromBond(Bond bond) {
        BondWithUserResponse response = new BondWithUserResponse();
        response.setId(bond.getId());
        response.setName(bond.getName());
        response.setNominalValue(bond.getNominalValue());
        response.setCouponRate(bond.getCouponRate());
        response.setMaturityPeriods(bond.getMaturityPeriods());
        response.setFrequency(bond.getFrequency());
        response.setMarketRate(bond.getMarketRate());
        response.setGracePeriods(bond.getGracePeriods());
        response.setGraceType(bond.getGraceType());
        response.setCurrency(bond.getCurrency());
        response.setInterestType(bond.getInterestType());
        response.setCapitalization(bond.getCapitalization());
        response.setCreatedAt(bond.getCreatedAt());
        
        if (bond.getUser() != null) {
            response.setUserId(bond.getUser().getId());
        }
        
        return response;
    }
} 