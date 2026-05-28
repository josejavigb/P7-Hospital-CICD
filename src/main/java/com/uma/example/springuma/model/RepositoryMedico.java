package com.uma.example.springuma.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositoryMedico extends JpaRepository<Medico, Long> {

    Medico getMedicoByDni(String dni);

}
