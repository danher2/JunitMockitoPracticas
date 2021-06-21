package org.dhernandez.appmockito.ejemplos.services;

import org.dhernandez.appmockito.ejemplos.models.Examen;
import org.dhernandez.appmockito.ejemplos.repositorios.ExamenRepositorio;
import org.dhernandez.appmockito.ejemplos.repositorios.PreguntasRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class) //extendemos nuestra clase de testjunit para que tmb ejecute con la extension de mockito y habilite las anotaciones
class ExamenServiceImplTest {

    //Mocks solo en metodos publicos o default

    @Mock  //para inyectar las los objectos mocks que crean las dependencias
    ExamenRepositorio repositorio;
    @Mock
    PreguntasRepository preguntasRepository;

    @InjectMocks//crea la instancia del service e injecta los dos objetos de arriba via constructor
    ExamenServiceImpl service; // se inyecta el tipo de la clase no la interfaz

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);//habilitamos el uso de anotaciones para esta clase


//       repositorio = mock(ExamenRepositorio.class); //nombre de la interfaz que queremos simular
//       preguntasRepository = mock(PreguntasRepository.class);
//       service = new ExamenServiceImpl(repositorio, preguntasRepository); //repositorio objeto simulado que no es el real



    }



    @Test
    void findExamenPorNombre() {



        when(repositorio.findAll()).thenReturn(Datos.EXAMENES);
       Optional<Examen> examen =  service.findExamenPorNombre("Matematicas");

        assertTrue(examen.isPresent());//probamos que existe el objet
        assertEquals(5L,examen.orElseThrow(null).getId());
        assertEquals("Matematicas",examen.get().getNombre());
    }

    @Test
    void findExamenPorNombreListaVacia() {


        List<Examen> datos  = Collections.emptyList();
        when(repositorio.findAll()).thenReturn(datos);
        Optional<Examen> examen =  service.findExamenPorNombre("Matematicas");

        assertFalse(examen.isPresent());//probamos que existe el objet
        }


    @Test
    void testPreguntasExamen() {
        when(repositorio.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntasRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        Examen examen = service.findExamenPorNombreConPreguntas("Historia");
    assertEquals(5,examen.getPreguntas().size());
    assertTrue(examen.getPreguntas().contains("aritmetica"));
    }

    @Test
    void testPreguntasExamenVerify() {
        when(repositorio.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntasRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        Examen examen = service.findExamenPorNombreConPreguntas("Historia");
        assertEquals(5,examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("aritmetica"));
        verify(repositorio).findAll();//verificamos que del objeto mock repositorio se ejecuta el findAll
        verify(preguntasRepository).findPreguntasPorExamenId(anyLong());
    }


    @Test
    void testNoExisteExamenVerify() {

        //Given
        when(repositorio.findAll()).thenReturn(Collections.emptyList());
        when(preguntasRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

        //When
        Examen examen = service.findExamenPorNombreConPreguntas("Historia");


        //Then
        assertNull(examen);
        verify(repositorio).findAll();//verificamos que del objeto mock repositorio se ejecuta el findAll
        verify(preguntasRepository).findPreguntasPorExamenId(anyLong());
    }

    @Test
    void testguardarExamen() {
        //Given
        Examen newExamen = Datos.EXAMEN;
        newExamen.setPreguntas(Datos.PREGUNTAS);
        when(repositorio.guardar(any(Examen.class))).then(new Answer<Examen>(){

            Long secuencia = 8L;

            @Override
            public Examen answer(InvocationOnMock invocation) throws Throwable {

               Examen examen = invocation.getArgument(0);
               examen.setId(secuencia++);

                return examen;
            }
        });

        //When
        Examen examen =  service.guardar(newExamen);


        //Then
        assertNotNull(examen.getId());
        assertEquals(8L, examen.getId());
        assertEquals("Fisica", examen.getNombre());
        verify(repositorio).guardar(any(Examen.class));
        verify(preguntasRepository).guardarVarias(anyList());


    }
}