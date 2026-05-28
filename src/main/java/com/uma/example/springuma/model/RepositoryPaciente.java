package com.uma.example.springuma.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositoryPaciente extends JpaRepository<Paciente, Long> {

    Paciente findByDni(String dni);

    List<Paciente> findByMedicoId(Long id);

}
