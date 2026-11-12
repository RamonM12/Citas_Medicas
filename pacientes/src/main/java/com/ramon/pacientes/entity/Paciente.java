package com.ramon.pacientes.entity;

import com.ramon.commons.enums.EstadoRegistro;
import com.ramon.commons.exceptions.ConflictException;
import com.ramon.commons.exceptions.DatoInvalidoException;
import com.ramon.commons.utils.StringCustomUtils;
import com.ramon.commons.utils.ValoresNumericosUtils;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "PACIENTES")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class Paciente {

    private static final String REGEX_EMAIL = "^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$";
    private static final String REGEX_TELEFONO = "^[0-9]{10}$";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PACIENTE")
    private Long id;

    @Column(name = "NOMBRE", nullable = false, length = 50)
    private String nombre;

    @Column(name = "APELLIDO_PATERNO", nullable = false, length = 50)
    private String apellidoPaterno;

    @Column(name = "APELLIDO_MATERNO", nullable = false, length = 50)
    private String apellidoMaterno;

    @Column(name = "EDAD", nullable = false)
    private Short edad;

    @Column(name = "PESO", nullable = false)
    private Double peso;

    @Column(name = "ESTATURA", nullable = false)
    private Double estatura;

    @Column(name = "IMC", nullable = false)
    private Double imc;

    @Column(name = "EMAIL", nullable = false, length = 100)
    private String email;

    @Column(name = "NUM_EXPEDIENTE", nullable = false, length = 20)
    private String numExpediente;

    @Column(name = "TELEFONO", nullable = false, length = 10)
    private String telefono;

    @Column(name = "DIRECCION", nullable = false, length = 150)
    private String direccion;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "ESTADO_REGISTRO", nullable = false, columnDefinition = "ESTADO_REGISTRO")
    private EstadoRegistro estadoRegistro;


    public String obtenerNombreCompleto() {
        return String.join(" ", this.nombre, this.apellidoPaterno, this.apellidoMaterno);
    }

    /** IMC = peso (kg) / estatura (m)^2, redondeado a 2 decimales. */
    private static Double calcularImc(Double peso, Double estatura) {
        return peso / (estatura * estatura);
    }

    private static String generarNumExpediente(String telefono) {
        StringBuilder sb = new StringBuilder(telefono.length() * 2);
        for (char digito : telefono.toCharArray()) {
            sb.append(digito).append('X');
        }
        return sb.toString();
    }

    public static void validarDatos(String nombre, String apellidoPaterno, String apellidoMaterno, Short edad,
                                    Double peso, Double estatura, String email,
                                    String telefono, String direccion) {
        StringCustomUtils.validarTamanio(nombre, 1, 50,
                "El nombre es requerido y debe tener entre 1 y 50 caracteres");

        StringCustomUtils.validarTamanio(apellidoPaterno, 1, 50,
                "El apellido paterno es requerido y debe tener entre 1 y 50 caracteres");

        StringCustomUtils.validarTamanio(apellidoMaterno, 1, 50,
                "El apellido materno es requerido y debe tener entre 1 y 50 caracteres");

        StringCustomUtils.validarTamanio(direccion, 1, 150,
                "La direccion es requerida y debe tener entre 1 y 150 caracteres");

        StringCustomUtils.validarTamanio(email, 1, 100,
                "El email es requerido y debe tener entre 1 y 100 caracteres");

        if (!email.trim().matches(REGEX_EMAIL))
            throw new DatoInvalidoException("El email debe tener el formato correcto (correo@dominio.com)");

        if (telefono == null || !telefono.trim().matches(REGEX_TELEFONO))
            throw new DatoInvalidoException("El telefono es requerido y debe contener exactamente 10 digitos");

        ValoresNumericosUtils.validarRangoShort(edad, (short) 1, (short) 100,
                "La edad es requerida y debe estar entre 1 y 100 años");

        validarRangoDouble(peso, 0.1, 200.0,
                "El peso es requerido y debe estar entre 0.1 y 200 kg");

        validarRangoDouble(estatura, 1.0, 2.0,
                "La estatura es requerida y debe estar entre 1.0 y 2.0 metros");

        // La tabla tiene CHECK_IMC (10 a 50); se valida aqui para dar un mensaje claro
        Double imc = calcularImc(peso, estatura);
        validarRangoDouble(imc, 10.0, 50.0,
                "El IMC calculado (" + imc + ") debe estar entre 10.0 y 50.0; verifica peso y estatura");
    }

    // Si en commons ya tienes ValoresNumericosUtils.validarRangoDouble, usa ese y borra este método
    private static void validarRangoDouble(Double valor, double min, double max, String mensaje) {
        if (valor == null || valor < min || valor > max)
            throw new DatoInvalidoException(mensaje);
    }

    private void validarNoEliminado() {
        if (this.estadoRegistro == EstadoRegistro.ELIMINADO)
            throw new ConflictException("El paciente ya esta eliminado");
    }

    public void eliminar() {
        validarNoEliminado();
        this.estadoRegistro = EstadoRegistro.ELIMINADO;
    }

    public void actualizar(String nombre, String apellidoPaterno, String apellidoMaterno, Short edad,
                           Double peso, Double estatura, String email,
                           String telefono, String direccion) {
        validarNoEliminado();

        validarDatos(nombre, apellidoPaterno, apellidoMaterno, edad, peso, estatura,
                email, telefono, direccion);

        this.nombre = nombre.trim();
        this.apellidoPaterno = apellidoPaterno.trim();
        this.apellidoMaterno = apellidoMaterno.trim();
        this.edad = edad;
        this.peso = peso;
        this.estatura = estatura;
        this.imc = calcularImc(peso, estatura);
        this.email = email.trim().toLowerCase();
        this.telefono = telefono.trim();
        this.numExpediente = generarNumExpediente(this.telefono); // se regenera si cambia el telefono
        this.direccion = direccion.trim();
    }

    public static Paciente crear(String nombre, String apellidoPaterno, String apellidoMaterno, Short edad,
                                 Double peso, Double estatura, String email,
                                 String telefono, String direccion) {
        validarDatos(nombre, apellidoPaterno, apellidoMaterno, edad, peso, estatura,
                email, telefono, direccion);

        String telefonoLimpio = telefono.trim();

        return Paciente.builder()
                .nombre(nombre.trim())
                .apellidoPaterno(apellidoPaterno.trim())
                .apellidoMaterno(apellidoMaterno.trim())
                .edad(edad)
                .peso(peso)
                .estatura(estatura)
                .imc(calcularImc(peso, estatura))
                .email(email.trim().toLowerCase())
                .telefono(telefonoLimpio)
                .numExpediente(generarNumExpediente(telefonoLimpio))
                .direccion(direccion.trim())
                .estadoRegistro(EstadoRegistro.ACTIVO)
                .build();
    }
}