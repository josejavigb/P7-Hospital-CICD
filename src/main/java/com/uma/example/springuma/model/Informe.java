package com.uma.example.springuma.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Informe {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "prediccion")
    private String prediccion;

    @Column(name = "contenido", columnDefinition = "TEXT")
    private String contenido;

    // Relación con imagen (muchas imágenes pueden pertenecer a un informe)
    @ManyToOne()
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "imagen_id")
    private Imagen imagen;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Imagen getImagen() {
        return imagen;
    }

    public void setImagen(Imagen id) {
        this.imagen = id;
    }

    public String getPrediccion() {
        return prediccion;
    }

    public void setPrediccion(String prediccion) {
        this.prediccion = prediccion;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public Informe() {
        // Constructor vacío necesario para JPA
    }

    public Informe(String prediccion, String contenido, Imagen imagen) {
        this.prediccion = prediccion;
        this.contenido = contenido;
        this.imagen = imagen;
    }

    public boolean equals(Object obj) {
        return (obj instanceof Informe) && ((Informe) obj).getContenido() == this.contenido;
    }

    public int hashCode() {
        return this.contenido.hashCode();
    }

    public String toString() {
        return "Predicción = " + this.prediccion + "; " + " Contenido = " + this.contenido + " imagen = " + this.imagen;
    }
}
