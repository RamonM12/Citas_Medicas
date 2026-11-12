package com.ramon.pacientes.service;

import com.ramon.commons.clients.CitaClient;
import com.ramon.commons.dto.pacientes.PacienteRequest;
import com.ramon.commons.dto.pacientes.PacienteResponse;
import com.ramon.commons.enums.EstadoRegistro;
import com.ramon.commons.exceptions.ConflictException;
import com.ramon.commons.exceptions.RecursoNoEncontradoException;
import com.ramon.pacientes.entity.Paciente;
import com.ramon.pacientes.mapper.PacienteMapper;
import com.ramon.pacientes.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PacienteServiceImpl implements PacienteService {

    private final PacienteRepository pacienteRepository;

    private final PacienteMapper pacienteMapper;

    private final CitaClient citaClient;

    @Override
    public List<PacienteResponse> listar() {
        log.info("Listando a todos los pacientes activos");

        return pacienteRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(pacienteMapper::entidadAResponse).toList();
    }

    @Override
    public PacienteResponse obtenerPorId(Long id) {
        return pacienteMapper.entidadAResponse(obtenerPacienteActivoPorId(id));
    }

    @Override
    public PacienteResponse obtenerPacientePorIdSinEstado(Long id) {
        log.info("Buscando paciente sin validar estado con ID: {}", id);

        return pacienteMapper.entidadAResponse(pacienteRepository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException("Paciente no encontrado con ID: " + id)
        ));
    }

    @Override
    public PacienteResponse registrar(PacienteRequest request) {
        log.info("Registrando nuevo paciente: {}", request.nombre());

        validarDatosUnicos(request);

        Paciente paciente = pacienteMapper.requestAEntidad(request);

        pacienteRepository.saveAndFlush(paciente);

        log.info("Nuevo paciente registrado: {}", paciente.obtenerNombreCompleto());

        return pacienteMapper.entidadAResponse(paciente);
    }

    @Override
    public PacienteResponse actualizar(PacienteRequest request, Long id) {
        Paciente paciente = obtenerPacienteActivoPorId(id);

        validarPacienteTieneCitasAgendadas(id);

        validarCambiosUnicos(request, id);

        log.info("Actualizando paciente con ID: {}", id);

        paciente.actualizar(
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

        pacienteRepository.saveAndFlush(paciente);

        log.info("Paciente con ID: {} actualizado correctamente", id);

        return pacienteMapper.entidadAResponse(paciente);
    }

    @Override
    public void eliminar(Long id) {
        Paciente paciente = obtenerPacienteActivoPorId(id);

        log.info("Eliminando paciente con ID: {}", id);

        validarPacienteTieneCitasAgendadas(id);

        paciente.eliminar();

        pacienteRepository.saveAndFlush(paciente);

        log.info("Paciente con ID: {} eliminado correctamente", id);
    }

    private Paciente obtenerPacienteActivoPorId(Long id) {
        log.info("Obteniendo paciente activo con ID: {}", id);

        return pacienteRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Paciente activo no encontrado con ID: " + id));
    }

    private void validarDatosUnicos(PacienteRequest request) {
        log.info("Validando email unico");

        if (pacienteRepository.existsByEmailIgnoreCaseAndEstadoRegistro(
                request.email().trim(), EstadoRegistro.ACTIVO))
            throw new ConflictException(
                    "Ya existe un paciente activo con el email: " + request.email());

        log.info("Validando telefono unico");

        if (pacienteRepository.existsByTelefonoAndEstadoRegistro(
                request.telefono().trim(), EstadoRegistro.ACTIVO))
            throw new ConflictException(
                    "Ya existe un paciente activo con el telefono: " + request.telefono());
    }

    private void validarCambiosUnicos(PacienteRequest request, Long id) {
        log.info("Validando cambio en email unico");

        if (pacienteRepository.existsByEmailIgnoreCaseAndEstadoRegistroAndIdNot(
                request.email().trim(), EstadoRegistro.ACTIVO, id))
            throw new ConflictException(
                    "Ya existe un paciente activo con el email: " + request.email());

        log.info("Validando cambio en telefono unico");

        if (pacienteRepository.existsByTelefonoAndEstadoRegistroAndIdNot(
                request.telefono().trim(), EstadoRegistro.ACTIVO, id))
            throw new ConflictException(
                    "Ya existe un paciente activo con el telefono: " + request.telefono());
    }

    private void validarPacienteTieneCitasAgendadas(Long id){
        log.info("Validando si el paciente con ID {}" +
                "tiene citas agendadas en servicio remoto..",id);

        citaClient.pacienteTieneCitasAsignadas(id);
    }
}