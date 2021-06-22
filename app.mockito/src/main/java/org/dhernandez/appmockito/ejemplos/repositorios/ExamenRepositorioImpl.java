package org.dhernandez.appmockito.ejemplos.repositorios;

import org.dhernandez.appmockito.ejemplos.Datos;
import org.dhernandez.appmockito.ejemplos.models.Examen;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ExamenRepositorioImpl implements ExamenRepositorio {
    @Override
    public List<Examen> findAll() {
        System.out.println("ExamenRepositorioImpl.findAll");

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();

        }

        return Datos.EXAMENES;
    }

    @Override
    public Examen guardar(Examen examen) {
        System.out.println("ExamenRepositorioImpl.guardar");
        return Datos.EXAMEN;
    }
}
