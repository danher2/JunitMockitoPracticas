package org.dhernandez.appmockito.ejemplos.repositorios;

import java.util.List;

public interface PreguntasRepository {

    List<String> findPreguntasPorExamenId(Long id);
    void guardarVarias(List<String> preguntas);

}
