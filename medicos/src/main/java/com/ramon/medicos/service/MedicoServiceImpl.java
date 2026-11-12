package com.ramon.medicos.service;

import com.ramon.commons.clients.CitaClient;
import com.ramon.commons.dto.medicos.MedicoRequest;
import com.ramon.commons.dto.medicos.MedicoResponse;
import com.ramon.commons.enums.DisponibilidadMedico;
import com.ramon.commons.enums.EspecialidadMedico;
import com.ramon.commons.enums.EstadoRegistro;
import com.ramon.commons.exceptions.ConflictException;
import com.ramon.commons.exceptions.RecursoNoEncontradoException;
import com.ramon.medicos.entity.Medico;
import com.ramon.medicos.mapper.MedicoMapper;
import com.ramon.medicos.repository.MedicoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MedicoServiceImpl implements MedicoService {

    private final MedicoRepository medicoRepository;

    private final MedicoMapper medicoMapper;

    private final CitaClient citaClient;

    @Override
    @Transactional(readOnly = true)
    public List<MedicoResponse> listar() {

        log.info("Listando todos los médicos activos");

        return medicoRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(medicoMapper::entidadAResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MedicoResponse obtenerPorId(Long id) {
        return medicoMapper.entidadAResponse(obtenerMedicoActivoPorId(id));
    }

    @Override
    @Transactional(readOnly = true)
    public MedicoResponse obtenerMedicoPorIdSinEstado(Long id) {

        log.info("Buscando médico sin estado con id {}", id);

        return medicoMapper.entidadAResponse(medicoRepository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException(
                        "Médico sin estado no encontrado con id: " + id)));
    }

    @Override
    public MedicoResponse registrar(MedicoRequest request) {

        log.info("Registrando nuevo médico: {}", request.nombre());

        validarDatosUnicos(request);

        Medico medico = medicoMapper.requestAEntidad(request);

        medico.actualizarEspecialidad(
                EspecialidadMedico.obtenerEspecialidadPorCodigo(
                        request.idEspecialidad()));

        medicoRepository.saveAndFlush(medico);

        log.info("Nuevo médico registrado: {}", medico.obtenerNombreCompleto());

        return medicoMapper.entidadAResponse(medico);
    }

    @Override
    public MedicoResponse actualizar(MedicoRequest request, Long id) {

        Medico medico = obtenerMedicoActivoPorId(id);

        validarMedicoTieneCitasAgendadas(id);

        validarCambiosUnicos(request, id);

        log.info("Actualizando médico con id: {}", id);

        medico.actualizar(
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno(),
                request.edad(),
                request.email(),
                request.telefono(),
                request.cedulaProfesional(),
                EspecialidadMedico.obtenerEspecialidadPorCodigo(request.idEspecialidad()));

        medicoRepository.saveAndFlush(medico);

        log.info("Médico con id {} actualizado correctamente", id);

        return medicoMapper.entidadAResponse(medico);
    }

    @Override
    public void actualizarDisponibilidadMedico(Long idMedico, Long idDisponibilidad) {

        Medico medico = obtenerMedicoActivoPorId(idMedico);

        log.info("Actualizando disponibilidad del médico con id: {}", idMedico);

        DisponibilidadMedico nuevaDisponibilidad = DisponibilidadMedico
                .obtenerDisponibilidadPorCodigo(idDisponibilidad);

        DisponibilidadMedico disponibilidadAnterior = medico.getDisponibilidad();

        medico.actualizarDisponibilidad(nuevaDisponibilidad);

        medicoRepository.saveAndFlush(medico);

        log.info("Disponibilidad del médico con id {} cambió de {} a {}",
                idMedico, disponibilidadAnterior, nuevaDisponibilidad);
    }

    @Override
    public void eliminar(Long id) {

        Medico medico = obtenerMedicoActivoPorId(id);

        log.info("Crucificando al médico con id: {}", id);

        validarMedicoTieneCitasAgendadas(id);

        medico.eliminar();

        medicoRepository.saveAndFlush(medico);

        log.info("Médico ha sido crucificado exitósamente");
    }

    private Medico obtenerMedicoActivoPorId(Long id) {

        log.info("Buscando médico activo con id {}", id);

        return medicoRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Médico activo no encontrado con id: " + id));
    }

    private void validarDatosUnicos(MedicoRequest request) {

        log.info("Validando email único...");

        if (medicoRepository.existsByEmailIgnoreCaseAndEstadoRegistro(
                request.email().trim(), EstadoRegistro.ACTIVO))
            throw new ConflictException(
                    "Ya existe un médico activo registrado con el email: " + request.email());

        log.info("Validando teléfono único...");

        if (medicoRepository.existsByTelefonoAndEstadoRegistro(
                request.telefono().trim(), EstadoRegistro.ACTIVO))
            throw new ConflictException(
                    "Ya existe un médico activo registrado con el teléfono: " + request.telefono());

        log.info("Validando cédula profesional única...");

        if (medicoRepository.existsByCedulaProfesionalIgnoreCaseAndEstadoRegistro(
                request.cedulaProfesional().trim(), EstadoRegistro.ACTIVO))
            throw new ConflictException(
                    "Ya existe un médico activo registrado con la cédula profesional: "
                            + request.cedulaProfesional());
    }

    private void validarCambiosUnicos(MedicoRequest request, Long id) {

        log.info("Validando cambio en email único...");

        if (medicoRepository.existsByEmailIgnoreCaseAndEstadoRegistroAndIdNot(
                request.email().trim(), EstadoRegistro.ACTIVO, id))
            throw new ConflictException(
                    "Ya existe un médico activo registrado con el email: " + request.email());

        log.info("Validando cambio en teléfono único...");

        if (medicoRepository.existsByTelefonoAndEstadoRegistroAndIdNot(
                request.telefono().trim(), EstadoRegistro.ACTIVO, id))
            throw new ConflictException(
                    "Ya existe un médico activo registrado con el teléfono: " + request.telefono());

        log.info("Validando cambio en cédula profesional única...");

        if (medicoRepository.existsByCedulaProfesionalIgnoreCaseAndEstadoRegistroAndIdNot(
                request.cedulaProfesional().trim(), EstadoRegistro.ACTIVO, id))
            throw new ConflictException(
                    "Ya existe un médico activo registrado con la cédula profesional: "
                            + request.cedulaProfesional());
    }

    private void validarMedicoTieneCitasAgendadas(Long id) {

        log.info("Validando si el médico con id {} " +
                "tiene citas agendadas en servicio remoto...", id);

        citaClient.medicoTieneCitasAsignadas(id);
    }
}