package com.ramon.citas.service;

import com.ramon.citas.dto.CitaRequest;
import com.ramon.citas.dto.CitaResponse;
import com.ramon.commons.service.CrudService;

public interface CitaService extends CrudService<CitaRequest, CitaResponse> {

    void actualizarEstadoCita(Long idCita, Long idEstadoCita);

    void medicoTieneCitasAsignadas(Long idMedico);

    void pacienteTieneCitasAsignadas(Long idPaciente);
}
