package com.ramon.medicos.service;

import com.ramon.commons.dto.medicos.MedicoRequest;
import com.ramon.commons.dto.medicos.MedicoResponse;
import com.ramon.commons.service.CrudService;

public interface MedicoService extends CrudService<MedicoRequest, MedicoResponse> {

    MedicoResponse obtenerMedicoPorIdSinEstado(Long id);

    void actualizarDisponibilidadMedico(Long idMedico, Long idDisponibilidad);
}
