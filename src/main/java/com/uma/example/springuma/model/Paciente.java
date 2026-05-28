package com.uma.example.springuma.model;

import java.io.Serializable;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // para ignorar el serializador al devolver un objeto cuenta
public class Paciente implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "edad")
    private int edad;

    @Column(name = "cita")
    private String cita; // Esto podría ser un tipo de fecha si es necesario

    @Column(name = "dni", unique = true)
    private String dni;

    @ManyToOne()
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Medico medico;

    // Constructor vacío
    public Paciente() {
    }

    // Constructor con todos los campos
    public Paciente(String nombre, int edad, String cita, String dni, Medico medico) {
        this.nombre = nombre;
        this.edad = edad;
        this.cita = cita;
        this.dni = dni;
        this.medico = medico;
    }

    // Getters y setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getCita() {
        return cita;
    }

    public void setCita(String cita) {
        this.cita = cita;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Paciente) && ((Paciente) obj).getDni().equals(this.dni);
    }

    @Override
    public int hashCode() {
        return dni != null ? dni.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Paciente{"
                + "id=" + id
                + ", nombre='" + nombre + '\''
                + ", edad=" + edad
                + ", cita='" + cita + '\''
                + ", dni='" + dni + '\''
                + ", medico=" + medico
                + '}';
    }

}
