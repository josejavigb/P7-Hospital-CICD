package com.uma.example.springuma.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // para ignorar el serializador al devolver un objeto cuenta
public class Medico implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(unique = true)
    private String dni;

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    @Column(name = "nombre")
    private String nombre;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Column(name = "especialidad")
    private String especialidad;

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public Medico() {

    }

    public Medico(String dni, String nombre, String especialidad) {
        this.dni = dni;
        this.nombre = nombre;
        this.especialidad = especialidad;
    }

    public boolean equals(Object obj) {
        return (obj instanceof Medico) && ((Medico) obj).getDni() == this.dni;
    }

    public int hashCode() {
        return this.dni.hashCode();
    }

    public String toString() {
        return "DNI " + this.dni + ": " + "nombre = " + this.nombre + ", especialidad = " + this.especialidad;
    }
}
