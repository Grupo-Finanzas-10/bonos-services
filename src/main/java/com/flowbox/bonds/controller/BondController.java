package com.flowbox.bonds.controller;

import com.flowbox.bonds.dto.BondWithUserResponse;
import com.flowbox.bonds.dto.SimulationResponse;
import com.flowbox.bonds.dto.FlujoCajaResponse;
import com.flowbox.bonds.model.Bond;
import com.flowbox.bonds.model.User;
import com.flowbox.bonds.repository.UserRepository;
import com.flowbox.bonds.service.BondService;
import com.flowbox.bonds.service.FinancialCalculator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/bonds")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Bonds", description = "API para gestión de bonos")
@SecurityRequirement(name = "bearerAuth")
public class BondController {

    private final BondService bondService;
    private final FinancialCalculator calculator;
    private final UserRepository userRepository;

    @GetMapping
    @Operation(summary = "Obtener todos los bonos", description = "Retorna la lista de bonos del usuario autenticado")
    public List<Bond> getAll() {
        // Obtener el usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        return bondService.findByUser(user);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener bono por ID", description = "Retorna un bono específico por su ID del usuario autenticado")
    public Optional<Bond> getById(@PathVariable Long id) {
        // Obtener el usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        return bondService.findByIdAndUser(id, user);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Obtener bonos por ID de usuario", description = "Retorna todos los bonos de un usuario específico por su ID")
    public List<BondWithUserResponse> getBondsByUserId(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        return bondService.findByUser(user)
                .stream()
                .map(BondWithUserResponse::fromBond)
                .collect(Collectors.toList());
    }

    @GetMapping("/user/username/{username}")
    @Operation(summary = "Obtener bonos por username", description = "Retorna todos los bonos de un usuario específico por su username")
    public List<BondWithUserResponse> getBondsByUsername(@PathVariable String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        return bondService.findByUser(user)
                .stream()
                .map(BondWithUserResponse::fromBond)
                .collect(Collectors.toList());
    }

    @GetMapping("/all")
    @Operation(summary = "Obtener todos los bonos", description = "Retorna todos los bonos del sistema con ID de usuario (solo para administradores)")
    public List<BondWithUserResponse> getAllBonds() {
        return bondService.findAll()
                .stream()
                .map(BondWithUserResponse::fromBond)
                .collect(Collectors.toList());
    }

    @PostMapping
    @Operation(summary = "Crear nuevo bono", description = "Crea un nuevo bono en el sistema asociado al usuario autenticado")
    public Bond create(@Valid @RequestBody Bond bond) {
        // Obtener el usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        bond.setCreatedAt(LocalDate.now());
        bond.setUser(user);
        return bondService.save(bond);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar bono", description = "Actualiza un bono específico del usuario autenticado")
    public Bond update(@PathVariable Long id, @Valid @RequestBody Bond bond) {
        // Obtener el usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Verificar que el bono pertenece al usuario
        Optional<Bond> existingBond = bondService.findByIdAndUser(id, user);
        if (existingBond.isEmpty()) {
            throw new RuntimeException("Bono no encontrado o no tienes permisos para modificarlo");
        }
        
        bond.setId(id);
        bond.setUser(user);
        return bondService.save(bond);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar bono", description = "Elimina un bono específico del usuario autenticado")
    public void delete(@PathVariable Long id) {
        // Obtener el usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Verificar que el bono pertenece al usuario
        Optional<Bond> existingBond = bondService.findByIdAndUser(id, user);
        if (existingBond.isEmpty()) {
            throw new RuntimeException("Bono no encontrado o no tienes permisos para eliminarlo");
        }
        
        bondService.delete(id);
    }

    @PostMapping("/simulate")
    @Operation(summary = "Simular cálculos financieros", description = "Calcula TCEA, TREA, Duración, Convexidad y Precio Máximo del bono")
    public SimulationResponse simulate(@RequestBody Bond bond, @RequestParam double tasaOportunidad) {
        double tcea = calculator.calcularTCEA(bond);
        double trea = calculator.calcularTREA(bond);
        double duracion = calculator.calcularDuracion(bond);
        double convexidad = calculator.calcularConvexidad(bond);
        double precioMaximo = calculator.calcularPrecioMaximo(bond, tasaOportunidad);
        double duracionModificada = calculator.calcularDuracionModificada(bond);
        
        return SimulationResponse.fromBond(bond, tcea, trea, duracion, convexidad, precioMaximo, duracionModificada);
    }

    @PostMapping("/simulate/cronograma")
    @Operation(summary = "Generar cronograma de flujos", description = "Genera el cronograma completo de flujos de caja del bono")
    public List<FlujoCajaResponse> generarCronograma(@RequestBody Bond bond) {
        return calculator.generarCronogramaFlujos(bond)
                .stream()
                .map(FlujoCajaResponse::fromFlujoCaja)
                .collect(Collectors.toList());
    }

    @PostMapping("/simulate/basic")
    @Operation(summary = "Simulación básica", description = "Calcula solo los valores financieros básicos (formato original)")
    public Map<String, Double> simulateBasic(@RequestBody Bond bond, @RequestParam double tasaOportunidad) {
        Map<String, Double> result = new HashMap<>();
        result.put("TCEA", calculator.calcularTCEA(bond));
        result.put("TREA", calculator.calcularTREA(bond));
        result.put("Duracion", calculator.calcularDuracion(bond));
        result.put("Convexidad", calculator.calcularConvexidad(bond));
        result.put("PrecioMaximo", calculator.calcularPrecioMaximo(bond, tasaOportunidad));
        return result;
    }
}
