package com.uma.example.springuma.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PacienteService {

    @Autowired
    RepositoryPaciente repositoryPaciente; 

    public List<Paciente> getAllPacientes() {
        return repositoryPaciente.findAll(); 
    }

    public Paciente getPaciente(Long id) {
        return repositoryPaciente.getReferenceById(id); 
    }

    public Paciente addPaciente(Paciente p) {
        return repositoryPaciente.saveAndFlush(p); 
    }

    public void updatePaciente(Paciente p) {
        /*Paciente paciente = repositoryPaciente.getReferenceById(p.getId());
        // Aquí podrías actualizar los campos específicos del paciente
        paciente.setNombre(p.getNombre());
        paciente.setEdad(p.getEdad());
        paciente.setCita(p.getCita());
        paciente.setDni(p.getDni());
        paciente.setMedico(p.getMedico());*/
        repositoryPaciente.save(p); // Guardamos el paciente actualizado
    }

    public void removePaciente(Paciente p) {
        repositoryPaciente.delete(p); 
    }

    public void removePacienteID(Long id) {
        repositoryPaciente.deleteById(id);
    }

    public List<Paciente> getPacientesMedico(Long id) {
        return repositoryPaciente.findByMedicoId(id);
    }
}
