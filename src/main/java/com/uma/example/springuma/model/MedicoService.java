package com.uma.example.springuma.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MedicoService {

    @Autowired
    RepositoryMedico repositoryMedico;

    public List<Medico> getAllMedicos() {
        return repositoryMedico.findAll();
    }

    public Medico getMedico(Long id) {
        return repositoryMedico.getReferenceById(id);
    }

    public Medico addMedico(Medico m) {
        return repositoryMedico.saveAndFlush(m);
    }

    public void updateMedico(Medico m) {
        repositoryMedico.save(m);
    }

    public void removeMedico(Medico m) {
        repositoryMedico.delete(m);
    }

    public void removeMedicoID(Long id) {
        repositoryMedico.deleteById(id);
    }

    public Medico getMedicoByDni(String dni) {
        return repositoryMedico.getMedicoByDni(dni);
    }
}
