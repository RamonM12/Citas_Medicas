package com.ramon.citas.service;

import com.ramon.citas.dto.CitaRequest;
import com.ramon.citas.dto.CitaResponse;
import com.ramon.citas.entity.Cita;
import com.ramon.citas.enums.EstadoCita;
import com.ramon.citas.mapper.CitaMapper;
import com.ramon.citas.repository.CitaRepository;
import com.ramon.commons.clients.MedicoClient;
import com.ramon.commons.clients.PacienteClient;
import com.ramon.commons.dto.medicos.MedicoResponse;
import com.ramon.commons.dto.pacientes.PacienteResponse;
import com.ramon.commons.enums.DisponibilidadMedico;
import com.ramon.commons.enums.EstadoRegistro;
import com.ramon.commons.exceptions.ConflictException;
import com.ramon.commons.exceptions.RecursoNoEncontradoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CitaServiceImpl implements CitaService {

    private final CitaRepository citaRepository;

    private final CitaMapper citaMapper;

    private final MedicoClient medicoClient;

    private final PacienteClient pacienteClient;

    private final List<EstadoCita> ESTADOS_ACTIVOS_INVALIDOS =
            List.of(EstadoCita.CONFIRMADA, EstadoCita.EN_CURSO);

    @Override
    @Transactional(readOnly = true)
    public List<CitaResponse> listar() {

        log.info("Listando todas las citas activas...");

        return citaRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(cita -> citaMapper.entidadAResponse(
                        cita,
                        obtenerPacienteSinEstado(cita.getIdPaciente()),
                        obtenerMedicoSinEstado(cita.getIdMedico())
                )).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CitaResponse obtenerPorId(Long id) {

        Cita cita = obtenerCitaOException(id);

        return citaMapper.entidadAResponse(cita,
                obtenerPacienteSinEstado(cita.getIdPaciente()),
                obtenerMedicoSinEstado(cita.getIdMedico()));
    }

    @Override
    @Transactional(readOnly = true)
    public void medicoTieneCitasAsignadas(Long idMedico) {

        log.info("Validando si el médico tiene una cita activa con los estados: {}",
                ESTADOS_ACTIVOS_INVALIDOS);

        if (citaRepository.existsByIdMedicoAndEstadoRegistroAndEstadoCitaIn(
                idMedico, EstadoRegistro.ACTIVO, ESTADOS_ACTIVOS_INVALIDOS))
            throw new ConflictException("No se puede modificar o eliminar el médico ya que " +
                    "tiene citas en estado: " + ESTADOS_ACTIVOS_INVALIDOS);
    }

    @Override
    @Transactional(readOnly = true)
    public void pacienteTieneCitasAsignadas(Long idPaciente){
        log.info("Validando si el paciente tiene una cita activa con los estados: {}", ESTADOS_ACTIVOS_INVALIDOS);

        if(citaRepository.existsByIdPacienteAndEstadoRegistroAndEstadoCitaIn(
                idPaciente,EstadoRegistro.ACTIVO,ESTADOS_ACTIVOS_INVALIDOS))
            throw new ConflictException("No se puede modificar o eliminar el paciente ya que " +
                    "tiene citas en estaod: " + ESTADOS_ACTIVOS_INVALIDOS);
    }

    private PacienteResponse obtenerPacienteActivo(Long id) {

        log.info("Buscando paciente activo con id {} en el servicio remoto...", id);

        return pacienteClient.obtenerPacienteActivoPorId(id);
    }

    private PacienteResponse obtenerPacienteSinEstado(Long id) {

        log.info("Buscando paciente sin estado con id {} en el servicio remoto...", id);

        return pacienteClient.obtenerPacienteSinEstadoPorId(id);
    }

    private void validarPacienteSinCitaActiva(Long idPaciente){
        log.info("Validando si el paciente ya tiene una cita activa");

        if(citaRepository.existsByIdPacienteAndEstadoRegistroAndEstadoCitaIn(
                idPaciente, EstadoRegistro.ACTIVO,
                List.of(EstadoCita.PENDIENTE,EstadoCita.CONFIRMADA,EstadoCita.EN_CURSO)))
            throw new ConflictException("El paciente ya tiene una cita activa");
    }

    @Override
    public CitaResponse registrar(CitaRequest request) {

        log.info("Registrando nueva cita...");

        PacienteResponse paciente = obtenerPacienteActivo(request.idPaciente());

        validarPacienteSinCitaActiva(request.idPaciente());

        MedicoResponse medico = obtenerMedicoActivo(request.idMedico());

        validarMedicoActivoDisponible(medico);

        Cita cita = citaMapper.requestAEntidad(request);

        citaRepository.saveAndFlush(cita);

        log.info("Cita registrada");

        cambiarDisponibilidadMedicoSegunEstadoCita(
                medico.id(), cita.getEstadoCita());

        return citaMapper.entidadAResponse(
                cita,
                paciente,
                medico);
    }

    @Override
    public CitaResponse actualizar(CitaRequest request, Long id) {

        Cita cita = obtenerCitaOException(id);

        if (cita.getEstadoCita() == EstadoCita.EN_CURSO)
            throw new ConflictException("La cita está en curso y no puede actualizarse");

        PacienteResponse paciente = obtenerPacienteActivo(request.idPaciente());

        MedicoResponse medico = obtenerMedicoActivo(request.idMedico());

        if (!cita.getIdPaciente().equals(request.idPaciente()))
            validarPacienteSinCitaActiva(request.idPaciente());

        if (!cita.getIdMedico().equals(request.idMedico()))
            validarMedicoActivoDisponible(medico);

        Long idMedicoAnterior = cita.getIdMedico();

        log.info("Actualizando cita con id: {}", id);

        cita.actualizar(
                request.idPaciente(),
                request.idMedico(),
                request.fechaCita(),
                request.sintomas());

        citaRepository.saveAndFlush(cita);

        log.info("Cita actualizada");

        if (!idMedicoAnterior.equals(cita.getIdMedico())) {
            actualizarDisponibilidadMedico(idMedicoAnterior, DisponibilidadMedico.DISPONIBLE.getCodigo());
            cambiarDisponibilidadMedicoSegunEstadoCita(cita.getIdMedico(), cita.getEstadoCita());
        }

        return citaMapper.entidadAResponse(
                cita,
                paciente,
                medico);
    }

    @Override
    public void actualizarEstadoCita(Long idCita, Long idEstadoCita) {

        Cita cita = obtenerCitaOException(idCita);

        log.info("Actualizando estado de la cita con id: {}", idCita);

        cita.actualizarEstadoCita(
                EstadoCita.obtenerEstadoCitaPorCodigo(idEstadoCita));

        citaRepository.saveAndFlush(cita);

        log.info("Estado de la cita {} actualizado", idCita);

        cambiarDisponibilidadMedicoSegunEstadoCita(
                cita.getIdMedico(),
                cita.getEstadoCita());
    }

    @Override
    public void eliminar(Long id) {

        Cita cita = obtenerCitaOException(id);

        log.info("Eliminando cita con id: {}", id);

        cita.eliminar();

        citaRepository.saveAndFlush(cita);

        log.info("Cita con id {} ha sido marcada como eliminada", id);

        if (cita.getEstadoCita() == EstadoCita.PENDIENTE)
            actualizarDisponibilidadMedico(
                    cita.getIdMedico(),
                    DisponibilidadMedico.DISPONIBLE.getCodigo());
    }

    private Cita obtenerCitaOException(Long id) {

        log.info("Buscando cita con id: {}", id);

        return citaRepository.findById(id).orElseThrow(() ->
                new RecursoNoEncontradoException("Cita no encontrada con id: " + id));
    }

    private MedicoResponse obtenerMedicoActivo(Long id) {

        log.info("Buscando médico activo con id {} en el servicio remoto...", id);

        return medicoClient.obtenerMedicoActivoPorId(id);
    }

    private MedicoResponse obtenerMedicoSinEstado(Long id) {

        log.info("Buscando médico sin estado con id {} en el servicio remoto...", id);

        return medicoClient.obtenerMedicoSinEstadoPorId(id);
    }

    private void actualizarDisponibilidadMedico(Long idMedico, Long idDisponibilidad) {

        log.info("Actualizando disponibilidad del médico en el servicio remoto...");

        medicoClient.actualizarDisponibilidadMedico(idMedico, idDisponibilidad);

        log.info("Disponibilidad del médico actualizada en el servicio remoto");
    }

    private void validarMedicoActivoDisponible(MedicoResponse medico) {

        log.info("Validando si el médico activo está disponible...");

        if (!DisponibilidadMedico.DISPONIBLE.getCodigo().equals(medico.idDisponibilidad()))
            throw new ConflictException("El médico activo no está disponible para consulta");
    }

    private void cambiarDisponibilidadMedicoSegunEstadoCita(Long idMedico, EstadoCita estadoCita) {

        switch (estadoCita) {

            case PENDIENTE, CONFIRMADA ->
                    actualizarDisponibilidadMedico(idMedico, DisponibilidadMedico.NO_DISPONIBLE.getCodigo());

            case FINALIZADA, CANCELADA ->
                    actualizarDisponibilidadMedico(idMedico, DisponibilidadMedico.DISPONIBLE.getCodigo());

            case EN_CURSO ->
                    actualizarDisponibilidadMedico(idMedico, DisponibilidadMedico.EN_CONSULTA.getCodigo());
        }
    }
}