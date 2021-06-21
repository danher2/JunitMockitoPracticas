package org.dhernandez.appmockito.ejemplos.services;

import org.dhernandez.appmockito.ejemplos.models.Examen;
import org.dhernandez.appmockito.ejemplos.repositorios.ExamenRepositorio;
import org.dhernandez.appmockito.ejemplos.repositorios.PreguntasRepository;

import java.util.List;
import java.util.Optional;

public class ExamenServiceImpl implements  ExamenService{


    private ExamenRepositorio examenRepo;
    private PreguntasRepository preguntasRepository;

    //constructor donde se inyectan los repositorios, se inicializan aqui en vez de usar autowird, porque? porque no se esta usando Spring
    public ExamenServiceImpl(ExamenRepositorio examenRepo, PreguntasRepository preguntasRepository) {

        this.examenRepo = examenRepo;
        this.preguntasRepository=preguntasRepository;
    }

    @Override
    public Optional<Examen> findExamenPorNombre(String nombre) {

       return examenRepo.findAll()
                .stream().filter(e->e.getNombre()
                .contains(nombre))
                .findFirst();


    }

    @Override
    public Examen findExamenPorNombreConPreguntas(String nombre) {
        Optional<Examen> examenOptional = findExamenPorNombre(nombre);
        Examen examen =null;
        if (examenOptional.isPresent()){
        examen= examenOptional.orElseThrow(null);
        List<String> preguntas = preguntasRepository.findPreguntasPorExamenId(examen.getId());
           examen .setPreguntas(preguntas);
        }

        return examen;
    }

    @Override
    public Examen guardar(Examen examen) {
        if (examen.getNombre().isEmpty() == false){

            preguntasRepository.guardarVarias(examen.getPreguntas());

        }

        return examenRepo.guardar(examen);
    }
}
