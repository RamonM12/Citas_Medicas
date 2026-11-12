package com.ramon.commons.dto.medicos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de un medico asciado a una cita")
public record DatosMedico(

        @Schema(description = "Nombre completo del medico", example = "Christian garcia lopez")
        String nombre,

        @Schema(description = "Cedula profesional del medico", example = "123456789")
        String cadulaProfesional,

        @Schema(description = "Nombre de la especialidad medica del medico", example = "Cardiologia")
        String especialdad
) {
}
