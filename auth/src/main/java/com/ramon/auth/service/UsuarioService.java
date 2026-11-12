package com.ramon.auth.service;

import com.ramon.auth.dto.UsuarioRequest;
import com.ramon.auth.dto.UsuarioResponse;
import com.ramon.auth.dto.UsuarioUpdateRequest;

import java.util.Set;

public interface UsuarioService {

    Set<UsuarioResponse> listar();

    UsuarioResponse registrar(UsuarioRequest request);

    UsuarioResponse actualizar(String username, UsuarioUpdateRequest request);

    void eliminar(String username);
}