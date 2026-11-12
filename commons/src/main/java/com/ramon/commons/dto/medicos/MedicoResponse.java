package com.ramon.commons.dto.medicos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Informacion de un medico")
public record MedicoResponse(
        @Schema(description = "Identificador unico del medico", example = "1")
        Long id,

        @Schema(description = "Nombre completo del medico", example = "Ramon mora")
        String nombre,

        @Schema(description = "Edad del medico", example = "35")
        Short edad,

        @Schema(description = "Correo electronico del medico", example = "ramonmora@gmail.com")
        String email,

        @Schema(description = "Numero telefonico del medico", example = "1234567890")
        String telefono,

        @Schema(description = "Cedula profesional del medico", example = "56589898")
        String cedulaProfesional,

        @Schema(description = "Nombre de la especialidad medica del medico", example = "Cardiologia")
        String especialidad,

        @Schema(description = "Descripcion de la disponibilidad del medico", example = "Disponible para atender pacientes")
        String disponilibidad,

        @Schema(description = "Identificador de la disponibilidad asociada al medico", example = "1")
        Long idDisponibilidad
) {
}
