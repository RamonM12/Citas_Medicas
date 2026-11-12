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
        return String.join(" ",
                this.getNombre(),
                this.getApellidoPaterno(),
                this.getApellidoMaterno());
    }

    private static Double calcularImc(Double peso, Double estatura) {
        return peso / (estatura * estatura);
    }

    private static String generarNumExpediente(String telefono) {
        String expediente = "";

        for (char digito : telefono.toCharArray()) {
            expediente += digito + "X";
        }

        return expediente;
    }

    public static void validarDatos(String nombre, String apellidoPaterno, String apellidoMaterno,
                                    Short edad, Double peso, Double estatura, String email,
                                    String telefono, String direccion) {

        StringCustomUtils.validarTamanio(nombre, 1, 50,
                "El nombre es requerido y debe contener entre 1 y 50 caracteres");

        StringCustomUtils.validarTamanio(apellidoPaterno, 1, 50,
                "El apellido paterno es requerido y debe contener entre 1 y 50 caracteres");

        StringCustomUtils.validarTamanio(apellidoMaterno, 1, 50,
                "El apellido materno es requerido y debe contener entre 1 y 50 caracteres");

        StringCustomUtils.validarTamanio(direccion, 1, 150,
                "La dirección es requerida y debe contener entre 1 y 150 caracteres");

        StringCustomUtils.validarTamanio(email, 1, 100,
                "El email es requerido y debe contener entre 1 y 100 caracteres");

        if (!email.trim().matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$"))
            throw new DatoInvalidoException("El email debe tener el formato correcto (correo@dominio.com)");

        if (telefono == null || !telefono.trim().matches("^[0-9]{10}$"))
            throw new DatoInvalidoException("El teléfono es requerido y debe contener exactamente 10 dígitos");

        ValoresNumericosUtils.validarRangoShort(edad, (short) 1, (short) 100,
                "La edad es requerida y debe ser entre 1 y 100 años");

        validarRangoDouble(peso, 0.1, 200.0,
                "El peso es requerido y debe ser entre 0.1 y 200 kg");

        validarRangoDouble(estatura, 1.0, 2.0,
                "La estatura es requerida y debe ser entre 1.0 y 2.0 metros");

        validarRangoDouble(calcularImc(peso, estatura), 10.0, 50.0,
                "El IMC debe ser entre 10 y 50, verifica el peso y la estatura");
    }

    private static void validarRangoDouble(Double valor, double min, double max, String mensaje) {
        if (valor == null || valor < min || valor > max)
            throw new DatoInvalidoException(mensaje);
    }

    private void validarNoEliminado() {
        if (this.estadoRegistro == EstadoRegistro.ELIMINADO)
            throw new ConflictException("El paciente ya está eliminado");
    }

    public void eliminar() {
        validarNoEliminado();
        this.estadoRegistro = EstadoRegistro.ELIMINADO;
    }

    public void actualizar(String nombre, String apellidoPaterno, String apellidoMaterno,
                           Short edad, Double peso, Double estatura, String email,
                           String telefono, String direccion) {

        validarNoEliminado();

        validarDatos(nombre, apellidoPaterno, apellidoMaterno,
                edad, peso, estatura, email, telefono, direccion);

        this.nombre = nombre.trim();
        this.apellidoPaterno = apellidoPaterno.trim();
        this.apellidoMaterno = apellidoMaterno.trim();
        this.edad = edad;
        this.peso = peso;
        this.estatura = estatura;
        this.imc = calcularImc(peso, estatura);
        this.email = email.trim().toLowerCase();
        this.telefono = telefono.trim();
        this.numExpediente = generarNumExpediente(telefono.trim());
        this.direccion = direccion.trim();
    }

    public static Paciente crear(String nombre, String apellidoPaterno, String apellidoMaterno,
                                 Short edad, Double peso, Double estatura, String email,
                                 String telefono, String direccion) {

        validarDatos(nombre, apellidoPaterno, apellidoMaterno,
                edad, peso, estatura, email, telefono, direccion);

        return Paciente.builder()
                .nombre(nombre.trim())
                .apellidoPaterno(apellidoPaterno.trim())
                .apellidoMaterno(apellidoMaterno.trim())
                .edad(edad)
                .peso(peso)
                .estatura(estatura)
                .imc(calcularImc(peso, estatura))
                .email(email.trim().toLowerCase())
                .telefono(telefono.trim())
                .numExpediente(generarNumExpediente(telefono.trim()))
                .direccion(direccion.trim())
                .estadoRegistro(EstadoRegistro.ACTIVO)
                .build();
    }
}