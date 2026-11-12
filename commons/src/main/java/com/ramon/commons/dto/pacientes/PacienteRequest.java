package com.ramon.commons.dto.pacientes;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Datos necesarios para registrar o actualizar un paciente")
public record PacienteRequest(
        @Schema(description = "Nombre del paciente", example = "Ramon")
        @NotBlank(message = "El nombre es requerido")
        @Size(min = 1, max = 50, message = "El nombre debe tener entre 1 y 50 caracteres")
        String nombre,

        @Schema(description = "Apellido paterno del paciente", example = "Mora")
        @NotBlank(message = "El apellido paterno es requerido")
        @Size(min = 1, max = 50, message = "El apellido paterno debe tener entre 1 y 50 caracteres")
        String apellidoPaterno,

        @Schema(description = "Apellido materno del paciente", example = "Cruz")
        @NotBlank(message = "El apellido materno es requerido")
        @Size(min = 1, max = 50, message = "El apellido materno debe tener entre 1 y 50 caracteres")
        String apellidoMaterno,

        @Schema(description = "Edad del paciente", example = "30", minimum = "1", maximum = "100")
        @NotNull(message = "La edad es requerida")
        @Min(value = 1, message = "La edad minima es de 1 año")
        @Max(value = 100, message = "La edad maxima es de 100 años")
        Short edad,

        @Schema(description = "Peso del paciente en kilogramos", example = "70.5", minimum = "0.1", maximum = "200")
        @NotNull(message = "El peso es requerido")
        @DecimalMin(value = "0.1", message = "El peso minimo es de 0.1 kg")
        @DecimalMax(value = "200.0", message = "El peso maximo es de 200 kg")
        Double peso,

        @Schema(description = "Estatura del paciente en metros", example = "1.75", minimum = "1.0", maximum = "2.0")
        @NotNull(message = "La estatura es requerida")
        @DecimalMin(value = "1.0", message = "La estatura minima es de 1.0 m")
        @DecimalMax(value = "2.0", message = "La estatura maxima es de 2.0 m")
        Double estatura,

        @Schema(description = "Correo electronico del paciente", example = "ramon@test.com")
        @NotBlank(message = "El email es requerido")
        @Size(min = 1, max = 100, message = "El email debe tener entre 1 y 100 caracteres")
        @Email(message = "El email debe tener el formato correcto (correo@dominio.com)")
        String email,

        @Schema(description = "Numero telefonico de contacto, debe contener exactamente 10 digitos",
                example = "5512345678", pattern = "^[0-9]{10}$")
        @NotBlank(message = "El telefono es requerido")
        @Pattern(regexp = "^[0-9]{10}$", message = "El telefono debe contener exactamente 10 digitos")
        String telefono,

        @Schema(description = "Direccion del paciente", example = "Av. Reforma 123, CDMX")
        @NotBlank(message = "La direccion es requerida")
        @Size(min = 1, max = 150, message = "La direccion debe tener entre 1 y 150 caracteres")
        String direccion
) {
}