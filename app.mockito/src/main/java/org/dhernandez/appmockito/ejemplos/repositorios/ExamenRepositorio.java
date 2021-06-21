package org.dhernandez.appmockito.ejemplos.repositorios;

import org.dhernandez.appmockito.ejemplos.models.Examen;

import java.util.List;

public interface ExamenRepositorio {

    List<Examen> findAll();
    Examen guardar(Examen examen);

}
