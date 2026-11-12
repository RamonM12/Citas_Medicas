package com.ramon.commons.dto.medicos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Datos necesarios para registrar o actualizar un medico")
public record MedicoRequest(
        @Schema(description = "Nombre del medico", example = "Ramon")
        @NotBlank(message = "El nombre es requerido")
        @Size(min = 1, max = 50, message = "El nombre debe tener entre 1 y 50 caracteres")
        String nombre,

        @Schema(description = "Apellido paterno del medico", example = "Garcia")
        @NotBlank(message = "El apellido paterno es requerido")
        @Size(min = 1, max = 50, message = "El apellido materno debe tener entre q y 50 caracteres")
        String apellidoPaterno,

        @Schema(description = "Apellido materno del medico", example = "Garcia")
        @NotBlank(message = "El apellido materno es requerido")
        @Size(min = 1, max = 50, message = "El apellido materno debe tener entre q y 50 caracteres")
        String apellidoMaterno,

        @Schema(description = "Edad del medico", example = "55", minimum = "18", maximum = "100")
        @NotNull(message = "La edad es requerido")
        @Min(value = 18, message = "La edad minima es de 18 años")
        @Max(value = 100, message = "La edad maxima es de 100 años")
        Short edad,

        @Schema(description = "Correo electornico del medico", example = "test@test.com")
        @NotBlank(message = "El email es requerido")
        @Size(min = 1, max = 50, message = "El email debe tener entre q y 50 caracteres")
        @Email(message = "El email debe tener el formato correcto(correo@dominio.com)")
        String email,

        @Schema(description = "Numero telefonico de contatcto, debe contener exactamente 10 digitos", example = "1234567890", pattern = "^[0-9]{10}$")
        @NotBlank(message = "El telefono es requerido")
        @Pattern(regexp = "^[0-9]{10}$" , message = "El telefono debe contener solo 10 digitos")
        String telefono,

        @Schema(description = "Cedula profesiona del medico, debe contener exactamente 12 caracteres", example = "123456789025")
        @NotBlank(message = "La cedula profesional es requerido")
        @Size(min = 12, max = 12, message = "La cedula profesional debe tener entre 12 caracteres")
        String cedulaProfesional,

        @Schema(description = "Identificador de la especialidad del medico", example = "5", minimum = "1")
        @NotNull(message = "El id de la especialidad es requerido")
        @Positive(message = "El identificador de la especialidad debe ser positiovo")
        Long idEspecialidad
) {
}
