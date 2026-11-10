package edu.uniminuto.inventario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ProductoSolicitud(
        @NotBlank String nombre,
        @NotNull @PositiveOrZero Integer stock) {
}
