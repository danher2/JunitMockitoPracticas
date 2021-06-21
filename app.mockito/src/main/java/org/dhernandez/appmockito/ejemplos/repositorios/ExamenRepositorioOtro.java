package org.dhernandez.appmockito.ejemplos.repositorios;

import org.dhernandez.appmockito.ejemplos.models.Examen;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ExamenRepositorioOtro implements ExamenRepositorio {
    @Override
    public List<Examen> findAll() {

        try {
            System.out.println("ExamenRepositorioOtro");
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();

        }

        return null;
    }

    @Override
    public Examen guardar(Examen examen) {
        return null;
    }
}
