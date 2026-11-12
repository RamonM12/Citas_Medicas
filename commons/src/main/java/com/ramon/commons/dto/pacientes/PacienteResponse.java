package com.ramon.commons.dto.pacientes;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Informacion de un paciente")
public record PacienteResponse(
        @Schema(description = "Identificador unico del paciente", example = "1")
        Long id,

        @Schema(description = "Nombre completo del paciente", example = "Ramon Mora Cruz")
        String nombre,

        @Schema(description = "Edad del paciente", example = "30")
        Short edad,

        @Schema(description = "Peso del paciente en kilogramos", example = "70.5")
        Double peso,

        @Schema(description = "Estatura del paciente en metros", example = "1.75")
        Double estatura,

        @Schema(description = "Indice de masa corporal calculado (peso / estatura²)", example = "23.02")
        Double imc,

        @Schema(description = "Correo electronico del paciente", example = "ramon@test.com")
        String email,

        @Schema(description = "Numero telefonico del paciente", example = "5512345678")
        String telefono,

        @Schema(description = "Direccion del paciente", example = "Av. Reforma 123, CDMX")
        String direccion,

        @Schema(description = "Numero de expediente generado a partir del telefono", example = "5X5X1X2X3X4X5X6X7X8X")
        String numExpediente
) {
}