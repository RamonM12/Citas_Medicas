package com.ramon.commons.dto.pacientes;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de un paciente asociado a una cita")
public record DatosPaciente(
        @Schema(description = "Nombre completo del paciente", example = "Ramon Mora Cruz")
        String nombre,

        @Schema(description = "Numero de expediente del paciente", example = "5X5X1X2X3X4X5X6X7X8X")
        String numExpediente,

        @Schema(description = "Edad del paciente", example = "30 años")
        String edad,

        @Schema(description = "Peso del paciente", example = "75.5 kg.")
        String peso,

        @Schema(description = "Estatura del paciente", example = "1.75 m.")
        String estatura,

        @Schema(description = "IMC del paciente", example = "24.653061224489797")
        String imc,

        @Schema(description = "Telefono del paciente", example = "5512345678")
        String telefono
) {
}