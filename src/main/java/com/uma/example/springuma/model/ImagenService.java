package com.uma.example.springuma.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.uma.example.springuma.utils.ImageUtils;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

@Service
public class ImagenService {

    @Autowired
    private RepositoryImagen repositoryImagen;

    public List<Imagen> getAllImagenes() {
        return repositoryImagen.findAll();
    }

    public Imagen getImagen(Long id) {
        return repositoryImagen.getReferenceById(id);
    }

    public String getNewPrediccion(Long id) throws IOException, Exception {
        /* API Deprecated
        Map<String, Double> response = 
        ImagenAPIPredictor.query(ImageUtils.decompressImage(repositoryImagen.getReferenceById(id).getFile_content()));
        System.out.println("resp");
        System.out.println( response);
        double score_0 = response.get("LABEL_0");
        double score_1 = response.get("LABEL_1");
        System.out.println("resp");
        System.out.println( response);*/
        double score_0 = Math.random();
        double score_1 = Math.random();
        String resulString;
        if (score_0 > score_1) {
            resulString = "{'status': 'Not cancer',  'score': " + score_0 + "}";
        } else {
            resulString = "{'status': 'Cancer',  'score': " + score_1 + "}";
        }
        return resulString;
    }

    public Imagen addImagen(Imagen imagen) {
        return repositoryImagen.saveAndFlush(imagen);
    }

    public void updateImagen(Imagen imagen) {
        repositoryImagen.save(imagen);
    }

    public void removeImagen(Imagen imagen) {
        repositoryImagen.delete(imagen);
    }

    public void removeImagenByID(Long id) {
        repositoryImagen.deleteById(id);
    }

    public List<Imagen> getImagenesPaciente(Long id) {
        return repositoryImagen.getByPacienteId(id);
    }

    public String uploadImage(MultipartFile file, Paciente paciente) throws IOException {
        Imagen imagen = new Imagen();
        imagen.setNombre(file.getOriginalFilename());
        imagen.setFile_content(ImageUtils.compressImage(file.getBytes()));
        imagen.setPaciente(paciente);
        imagen.setFecha(Calendar.getInstance());
        imagen = repositoryImagen.saveAndFlush(imagen);
        if (imagen != null) {
            return "{\"response\" : \"file uploaded successfully : " + file.getOriginalFilename() + "\"}";
        }
        return null;
    }

    public byte[] downloadImage(long id) {
        Imagen dbImageData = repositoryImagen.getReferenceById(id);
        byte[] images = ImageUtils.decompressImage(dbImageData.getFile_content());
        return images;
    }

}
