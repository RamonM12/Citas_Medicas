package com.ramon.pacientes.service;

import com.ramon.commons.dto.pacientes.PacienteRequest;
import com.ramon.commons.dto.pacientes.PacienteResponse;
import com.ramon.commons.service.CrudService;

public interface PacienteService extends CrudService<PacienteRequest, PacienteResponse> {
    PacienteResponse obtenerPacientePorIdSinEstado(Long id);
}