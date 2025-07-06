package com.flowbox.bonds.service;

import com.flowbox.bonds.model.Bond;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class FinancialCalculator {

    /**
     * Calcula TCEA (Tasa de Coste Efectivo Anual) desde perspectiva del emisor
     * En método americano, si no hay costos de emisión, TCEA = couponRate
     */
    public double calcularTCEA(Bond bond) {
        // Para método americano sin costos de emisión
        // TCEA = tasa que iguala flujos de salida del emisor con ingresos
        
        // Flujos de salida del emisor:
        // - Recibe: nominalValue (al emitir)
        // - Paga: cupones periódicos + nominal al final
        
        // Si no hay costos de emisión, TCEA = couponRate
        return bond.getCouponRate();
    }

    /**
     * Calcula TREA (Tasa de Rendimiento Efectivo Anual) desde perspectiva del inversionista
     * En método americano, TREA = marketRate (tasa de oportunidad)
     */
    public double calcularTREA(Bond bond) {
        // TREA es la tasa que iguala el precio pagado con los flujos recibidos
        // Si el inversionista compra al precio de mercado, TREA = marketRate
        return bond.getMarketRate();
    }

    /**
     * Calcula la Duración (Duration) del bono
     * Duración = Σ(t × FlujoPeriodo / (1+r)^t) / PrecioBono
     */
    public double calcularDuracion(Bond bond) {
        double tasaDescuento = bond.getMarketRate();
        double tasaPeriodica = tasaDescuento / bond.getFrequency();
        double cupon = bond.getNominalValue() * (bond.getCouponRate() / bond.getFrequency());
        
        // Aplicar períodos de gracia si existen
        int periodosEfectivos = bond.getMaturityPeriods();
        double valorNominalEfectivo = bond.getNominalValue();
        
        if (bond.getGracePeriods() > 0 && "TOTAL".equals(bond.getGraceType())) {
            // Capitalizar durante gracia total
            double tasaCapitalizacion = bond.getCouponRate() / bond.getCapitalization();
            valorNominalEfectivo = bond.getNominalValue() * 
                Math.pow(1 + tasaCapitalizacion, bond.getGracePeriods());
            cupon = valorNominalEfectivo * (bond.getCouponRate() / bond.getFrequency());
        }
        
        double sumaPonderada = 0;
        double precioBono = calcularPrecioMaximo(bond, tasaDescuento);
        
        // Períodos de gracia (si aplica)
        int inicioFlujos = bond.getGracePeriods() + 1;
        
        // Calcular duración para flujos normales
        for (int t = inicioFlujos; t <= periodosEfectivos; t++) {
            double flujo = cupon;
            if (t == periodosEfectivos) {
                flujo += valorNominalEfectivo; // Último período incluye principal
            }
            
            double valorPresente = flujo / Math.pow(1 + tasaPeriodica, t);
            sumaPonderada += t * valorPresente;
        }
        
        return sumaPonderada / precioBono;
    }

    /**
     * Calcula la Convexidad del bono
     * Convexidad = Σ(t × (t+1) × FlujoPeriodo / (1+r)^(t+2)) / PrecioBono
     */
    public double calcularConvexidad(Bond bond) {
        double tasaDescuento = bond.getMarketRate();
        double tasaPeriodica = tasaDescuento / bond.getFrequency();
        double cupon = bond.getNominalValue() * (bond.getCouponRate() / bond.getFrequency());
        
        // Aplicar períodos de gracia si existen
        int periodosEfectivos = bond.getMaturityPeriods();
        double valorNominalEfectivo = bond.getNominalValue();
        
        if (bond.getGracePeriods() > 0 && "TOTAL".equals(bond.getGraceType())) {
            double tasaCapitalizacion = bond.getCouponRate() / bond.getCapitalization();
            valorNominalEfectivo = bond.getNominalValue() * 
                Math.pow(1 + tasaCapitalizacion, bond.getGracePeriods());
            cupon = valorNominalEfectivo * (bond.getCouponRate() / bond.getFrequency());
        }
        
        double sumaConvexidad = 0;
        double precioBono = calcularPrecioMaximo(bond, tasaDescuento);
        int inicioFlujos = bond.getGracePeriods() + 1;
        
        for (int t = inicioFlujos; t <= periodosEfectivos; t++) {
            double flujo = cupon;
            if (t == periodosEfectivos) {
                flujo += valorNominalEfectivo;
            }
            
            double valorPresente = flujo / Math.pow(1 + tasaPeriodica, t + 2);
            sumaConvexidad += t * (t + 1) * valorPresente;
        }
        
        return sumaConvexidad / precioBono;
    }

    /**
     * Calcula el Precio Máximo del bono (valor presente de flujos futuros)
     * Método Americano: cupones periódicos + principal al final
     */
    public double calcularPrecioMaximo(Bond bond, double tasaOportunidad) {
        double tasaPeriodica = tasaOportunidad / bond.getFrequency();
        double cupon = bond.getNominalValue() * (bond.getCouponRate() / bond.getFrequency());
        
        // Manejar períodos de gracia
        int periodosEfectivos = bond.getMaturityPeriods();
        double valorNominalEfectivo = bond.getNominalValue();
        
        if (bond.getGracePeriods() > 0 && "TOTAL".equals(bond.getGraceType())) {
            // Capitalización durante gracia total
            double tasaCapitalizacion = bond.getCouponRate() / bond.getCapitalization();
            valorNominalEfectivo = bond.getNominalValue() * 
                Math.pow(1 + tasaCapitalizacion, bond.getGracePeriods());
            cupon = valorNominalEfectivo * (bond.getCouponRate() / bond.getFrequency());
        }
        
        double precio = 0;
        int inicioFlujos = bond.getGracePeriods() + 1;
        
        // Valor presente de cupones
        for (int t = inicioFlujos; t <= periodosEfectivos; t++) {
            precio += cupon / Math.pow(1 + tasaPeriodica, t);
        }
        
        // Valor presente del principal (al final)
        precio += valorNominalEfectivo / Math.pow(1 + tasaPeriodica, periodosEfectivos);
        
        return precio;
    }

    /**
     * Sobrecarga para usar marketRate como tasa de oportunidad
     */
    public double calcularPrecioMaximo(Bond bond) {
        return calcularPrecioMaximo(bond, bond.getMarketRate());
    }

    /**
     * Calcula la Duración Modificada
     * DuraciónModificada = Duración / (1 + r)
     */
    public double calcularDuracionModificada(Bond bond) {
        double duracion = calcularDuracion(bond);
        double tasaPeriodica = bond.getMarketRate() / bond.getFrequency();
        return duracion / (1 + tasaPeriodica);
    }

    /**
     * Genera el cronograma de flujos de caja para el método americano
     */
    public List<FlujoCaja> generarCronogramaFlujos(Bond bond) {
        List<FlujoCaja> flujos = new ArrayList<>();
        
        double cupon = bond.getNominalValue() * (bond.getCouponRate() / bond.getFrequency());
        double valorNominalEfectivo = bond.getNominalValue();
        
        // Manejar períodos de gracia
        if (bond.getGracePeriods() > 0 && "TOTAL".equals(bond.getGraceType())) {
            // Períodos de gracia total - capitalización
            double tasaCapitalizacion = bond.getCouponRate() / bond.getCapitalization();
            
            for (int t = 1; t <= bond.getGracePeriods(); t++) {
                flujos.add(new FlujoCaja(t, 0, 0, 0, "Gracia Total"));
            }
            
            // Recalcular valor nominal después de gracia
            valorNominalEfectivo = bond.getNominalValue() * 
                Math.pow(1 + tasaCapitalizacion, bond.getGracePeriods());
            cupon = valorNominalEfectivo * (bond.getCouponRate() / bond.getFrequency());
        }
        
        // Flujos normales del método americano
        int inicioFlujos = bond.getGracePeriods() + 1;
        
        for (int t = inicioFlujos; t <= bond.getMaturityPeriods(); t++) {
            double principal = 0;
            String descripcion = "Cupón";
            
            if (t == bond.getMaturityPeriods()) {
                principal = valorNominalEfectivo;
                descripcion = "Cupón + Principal";
            }
            
            double flujoTotal = cupon + principal;
            flujos.add(new FlujoCaja(t, cupon, principal, flujoTotal, descripcion));
        }
        
        return flujos;
    }

    /**
     * Clase para representar un flujo de caja
     */
    public static class FlujoCaja {
        private int periodo;
        private double interes;
        private double principal;
        private double flujoTotal;
        private String descripcion;

        public FlujoCaja(int periodo, double interes, double principal, double flujoTotal, String descripcion) {
            this.periodo = periodo;
            this.interes = interes;
            this.principal = principal;
            this.flujoTotal = flujoTotal;
            this.descripcion = descripcion;
        }

        // Getters y setters
        public int getPeriodo() { return periodo; }
        public void setPeriodo(int periodo) { this.periodo = periodo; }
        
        public double getInteres() { return interes; }
        public void setInteres(double interes) { this.interes = interes; }
        
        public double getPrincipal() { return principal; }
        public void setPrincipal(double principal) { this.principal = principal; }
        
        public double getFlujoTotal() { return flujoTotal; }
        public void setFlujoTotal(double flujoTotal) { this.flujoTotal = flujoTotal; }
        
        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    }
}
