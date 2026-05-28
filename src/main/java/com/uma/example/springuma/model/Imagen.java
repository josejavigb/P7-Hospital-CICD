package com.uma.example.springuma.model;

import java.util.Calendar;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // para ignorar el serializador al devolver un objeto cuenta
public class Imagen {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "fecha")
    private Calendar fecha;

    public Calendar getFecha() {
        return fecha;
    }

    public void setFecha(Calendar fecha) {
        this.fecha = fecha;
    }
    // Relación con Paciente (muchas imágenes pueden pertenecer a un paciente)
    @ManyToOne()
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    @Lob
    @Column(name = "file_content")
    private byte[] file_content;

    public byte[] getFile_content() {
        return file_content;
    }

    public void setFile_content(byte[] file_content) {
        this.file_content = file_content;
    }

    // Constructor vacío
    public Imagen() {
    }

    // Getters y Setters
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

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Imagen)) {
            return false;
        }
        return id != 0 && id == (((Imagen) o).getId());
    }

    @Override
    public int hashCode() {
        return this.nombre.hashCode();
    }

    @Override
    public String toString() {
        return "Imagen{"
                + "id=" + id
                + ", nombre='" + nombre + '\''
                + ", paciente=" + paciente
                + '}';
    }

    public Imagen(byte[] file_content, Paciente paciente) {
        this.file_content = file_content;
        this.paciente = paciente;
    }

}
