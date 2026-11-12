package com.ramon.commons.enums;

import com.ramon.commons.exceptions.DatoInvalidoException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Objects;

@RequiredArgsConstructor
@Getter
public enum DisponibilidadMedico {
    DISPONIBLE(1L,"Disponible para atender pacientes"),
    EN_CONSULTA(2L, "Atendiendo a un paciente"),
    FUERA_DE_TURNO(3L,"No se encuentra en turno"),
    DE_GUARDIA(4L, "Disponible bajo guardia"),
    NO_DISPONIBLE(5L, "No disponible por el momento");

    private final Long codigo;
    private final String descripcion;

    public static DisponibilidadMedico obtenerDisponibilidadPorCodigo(Long codigo){
       return Arrays.stream(values())
               .filter(d->Objects.equals(d.codigo,codigo))
               .findFirst()
               .orElseThrow(()-> new DatoInvalidoException("Codigo de disponibilidad no valido" + codigo));
    }
}
