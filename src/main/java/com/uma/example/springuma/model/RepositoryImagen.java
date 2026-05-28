package com.uma.example.springuma.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositoryImagen extends JpaRepository<Imagen, Long> {

    List<Imagen> getByPacienteId(Long id);

    // Aquí puedes añadir métodos personalizados si es necesario.
    // Por ejemplo, si quieres buscar imágenes por una propiedad específica:
    // List<Imagen> findByPropiedad(String propiedad);
    // Si no necesitas métodos personalizados, la interfaz puede quedar vacía,
    // ya que hereda todos los métodos necesarios de JpaRepository.
}
