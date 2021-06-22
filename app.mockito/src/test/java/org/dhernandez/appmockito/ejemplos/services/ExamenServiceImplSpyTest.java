package org.dhernandez.appmockito.ejemplos.services;

import org.dhernandez.appmockito.ejemplos.Datos;
import org.dhernandez.appmockito.ejemplos.models.Examen;
import org.dhernandez.appmockito.ejemplos.repositorios.ExamenRepositorio;
import org.dhernandez.appmockito.ejemplos.repositorios.ExamenRepositorioImpl;
import org.dhernandez.appmockito.ejemplos.repositorios.PreguntasRepository;
import org.dhernandez.appmockito.ejemplos.repositorios.PreguntasRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) //extendemos nuestra clase de testjunit para que tmb ejecute con la extension de mockito y habilite las anotaciones
class ExamenServiceImplSpyTest {

    //Mocks solo en metodos publicos o default

    @Spy  //para inyectar las los objectos mocks que crean las dependencias
    ExamenRepositorioImpl repositorio;
    @Spy
    PreguntasRepositoryImpl preguntasRepository;

    @InjectMocks//crea la instancia del service e injecta los dos objetos de arriba via constructor
    ExamenServiceImpl service; // se inyecta el tipo de la clase no la interfaz






    //Spy llama metodos reales y se le puede cambiar la impl y usa de clases no de interfaces
    //Mock es 100% simulado
    @Test
    void testSpy() {
        //creamos los objetos en lo metodos
    //inyectando la dependencia por constructor (en vez de usar annotations)

    List<String> preguntas = Arrays.asList("aritmetica");


    //hibrido
//    when(preguntasRepository.findPreguntasPorExamenId(anyLong())).thenReturn(preguntas);

        doReturn(preguntas).when(preguntasRepository).findPreguntasPorExamenId(anyLong());

        Examen examen = service.findExamenPorNombreConPreguntas("Matematicas");
    assertEquals(5,examen.getId());
    assertEquals("Matematicas", examen.getNombre());
    assertEquals(1,examen.getPreguntas().size());
    assertTrue(examen.getPreguntas().contains("aritmetica"));

    //verify
    verify(repositorio).findAll();
    verify(preguntasRepository).findPreguntasPorExamenId(anyLong());

    }
}