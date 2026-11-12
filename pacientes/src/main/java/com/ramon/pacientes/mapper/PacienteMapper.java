package com.ramon.pacientes.mapper;

import com.ramon.commons.dto.pacientes.PacienteRequest;
import com.ramon.commons.dto.pacientes.PacienteResponse;
import com.ramon.commons.mapper.CommonMapper;
import com.ramon.pacientes.entity.Paciente;
import org.springframework.stereotype.Component;

@Component
public class PacienteMapper implements CommonMapper<PacienteRequest, PacienteResponse, Paciente> {

    @Override
    public Paciente requestAEntidad(PacienteRequest request) {
        if (request == null) return null;

        return Paciente.crear(
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno(),
                request.edad(),
                request.peso(),
                request.estatura(),
                request.email(),
                request.telefono(),
                request.direccion()
        );
    }

    @Override
    public PacienteResponse entidadAResponse(Paciente entidad) {
        if (entidad == null) return null;

        return new PacienteResponse(
                entidad.getId(),
                entidad.obtenerNombreCompleto(),
                entidad.getEdad(),
                entidad.getPeso(),
                entidad.getEstatura(),
                entidad.getImc(),
                entidad.getEmail(),
                entidad.getTelefono(),
                entidad.getDireccion(),
                entidad.getNumExpediente()
        );
    }
}