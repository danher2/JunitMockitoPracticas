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
    ExamenRepositorioImpl repositorio;
    @Mock
    PreguntasRepositoryImpl preguntasRepository;

    @InjectMocks//crea la instancia del service e injecta los dos objetos de arriba via constructor
    ExamenServiceImpl service; // se inyecta el tipo de la clase no la interfaz

    @Captor
    ArgumentCaptor<Long> captor;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);//habilitamos el uso de anotaciones para esta clase


//       repositorio = mock(ExamenRepositorioImpl.class); //nombre de la interfaz que queremos simular
//       preguntasRepository = mock(PreguntasRepositoryImpl.class);
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

    @Test
    void testManejoException() {
        when(repositorio.findAll()).thenReturn(Datos.ID_NULL);
        when(preguntasRepository.findPreguntasPorExamenId(isNull())).thenThrow(IllegalArgumentException.class);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {

            service.findExamenPorNombreConPreguntas("Matematicas");
        });

        assertEquals(IllegalArgumentException.class, exception.getClass());

        verify(repositorio).findAll();
        verify(preguntasRepository).findPreguntasPorExamenId(isNull());

    }


    @Test
    void testArgumentMatches() {
        when(repositorio.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntasRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        service.findExamenPorNombreConPreguntas("Matematicas");
        verify(repositorio).findAll();
//        verify(preguntasRepository).findPreguntasPorExamenId(ArgumentMatchers.argThat(arg->arg!=null && arg.equals(5L)));
        verify(preguntasRepository).findPreguntasPorExamenId(ArgumentMatchers.argThat(arg->arg != null && arg>=5L));
//        verify(preguntasRepository).findPreguntasPorExamenId(eq(5l));

    }


    @Test
    void testArgumentMatches2() {
        when(repositorio.findAll()).thenReturn(Datos.ID_NEGATIVOS);
        when(preguntasRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        service.findExamenPorNombreConPreguntas("Matematicas");
        verify(repositorio).findAll();
        verify(preguntasRepository).findPreguntasPorExamenId(argThat(new MiArgsMatchers()));

    }

    @Test
    void testArgumentMatches3() {
        when(repositorio.findAll()).thenReturn(Datos.ID_NEGATIVOS);
        when(preguntasRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        service.findExamenPorNombreConPreguntas("Matematicas");
        verify(repositorio).findAll();
        verify(preguntasRepository).findPreguntasPorExamenId(argThat(argument -> argument != null && argument > 0));

    }


    //clase inner
    public static class MiArgsMatchers implements ArgumentMatcher<Long> {

        private Long argument;

        @Override
        public boolean matches(Long argument) {
            this.argument= argument;
            return argument != null && argument > 0;
        }

        @Override
        public String toString() {
            return "es para un mensaje personalizado de error" +
                    "que imprime mockito en caso de que falle el test " +
                    argument +  " debe ser un entero positivo";
        }

    }


    @Test
    void testArgumentCaptor() {

        when(repositorio.findAll()).thenReturn(Datos.EXAMENES);
//        when(preguntasRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        service.findExamenPorNombreConPreguntas("Matematicas");

//        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);

        verify(preguntasRepository).findPreguntasPorExamenId(captor.capture());


        assertEquals(5L,captor.getValue());
    }

    @Test
    void testDoThrow() {
        Examen examen = Datos.EXAMEN;
        examen.setPreguntas(Datos.PREGUNTAS);

        //que pasa cuando el metodo es void y quiero manejar una excepcion, entnces:
      doThrow(IllegalArgumentException.class).when(preguntasRepository).guardarVarias(anyList());
    assertThrows(IllegalArgumentException.class, ()->{
       service.guardar(examen);
    });
    }

    @Test
    void testDoAnswer() {
        when(repositorio.findAll()).thenReturn(Datos.EXAMENES);
//        when(preguntasRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

        doAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return id == 5L? Datos.PREGUNTAS: Collections.emptyList();
        }).when(preguntasRepository).findPreguntasPorExamenId(anyLong());

        Examen examen = service.findExamenPorNombreConPreguntas("Matematicas");
        assertEquals(5,examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("geometria") );
        assertEquals(5L, examen.getId());
        assertEquals("Matematicas", examen.getNombre());

        verify(preguntasRepository).findPreguntasPorExamenId(anyLong());


    }


    //DoAnswer
    @Test
    void testDoAnswerguardarExamen() {
        //Given
        Examen newExamen = Datos.EXAMEN;
        newExamen.setPreguntas(Datos.PREGUNTAS);


        doAnswer(new Answer<Examen>(){

            Long secuencia = 8L;

            @Override
            public Examen answer(InvocationOnMock invocation) throws Throwable {

                Examen examen = invocation.getArgument(0);
                examen.setId(secuencia++);

                return examen;
            }
        }).when(repositorio).guardar(any(Examen.class));


        //When
        Examen examen =  service.guardar(newExamen);


        //Then
        assertNotNull(examen.getId());
        assertEquals(8L, examen.getId());
        assertEquals("Fisica", examen.getNombre());
        verify(repositorio).guardar(any(Examen.class));
        verify(preguntasRepository).guardarVarias(anyList());


    }

    //Invocar el metodo real
    @Test
    void testDoCallRealMethod() {

    when(repositorio.findAll()).thenReturn(Datos.EXAMENES);
//  when(preguntasRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

    doCallRealMethod().when(preguntasRepository).findPreguntasPorExamenId(anyLong());
    Examen examen = service.findExamenPorNombreConPreguntas("Matematicas");
    assertEquals(5L, examen.getId());
    assertEquals("Matematicas",examen.getNombre());

    }

    //Spy llama metodos reales y se le puede cambiar la impl y usa de clases no de interfaces
    //Mock es 100% simulado
    @Test
    void testSpy() {
        //creamos los objetos en lo metodos
    ExamenRepositorio examenRepositorio = spy(ExamenRepositorioImpl.class);
    PreguntasRepository preguntasRepository = spy(PreguntasRepositoryImpl.class);
    //inyectando la dependencia por constructor (en vez de usar annotations)
    ExamenService examenService = new ExamenServiceImpl(examenRepositorio,preguntasRepository);

    List<String> preguntas = Arrays.asList("aritmetica");


    //hibrido
//    when(preguntasRepository.findPreguntasPorExamenId(anyLong())).thenReturn(preguntas);

        doReturn(preguntas).when(preguntasRepository).findPreguntasPorExamenId(anyLong());

        Examen examen = examenService.findExamenPorNombreConPreguntas("Matematicas");
    assertEquals(5,examen.getId());
    assertEquals("Matematicas", examen.getNombre());
    assertEquals(1,examen.getPreguntas().size());
    assertTrue(examen.getPreguntas().contains("aritmetica"));

    //verify
    verify(examenRepositorio).findAll();
    verify(preguntasRepository).findPreguntasPorExamenId(anyLong());

    }

    @Test
    void testOrdenInvocaciones() {
        when(repositorio.findAll()).thenReturn(Datos.EXAMENES);

        service.findExamenPorNombreConPreguntas("Matematicas");
        service.findExamenPorNombreConPreguntas("Lenguaje");
        InOrder inOrder = inOrder(preguntasRepository);

        //validamos que se invoque primero metematicas y despues lenguaje
        inOrder.verify(preguntasRepository).findPreguntasPorExamenId(6L);
        inOrder.verify(preguntasRepository).findPreguntasPorExamenId(5L);

    }


    @Test
    void testOrdenInvocaciones2() {
        when(repositorio.findAll()).thenReturn(Datos.EXAMENES);

        service.findExamenPorNombreConPreguntas("Matematicas");
        service.findExamenPorNombreConPreguntas("Lenguaje");
        //verificamos varios mocks
        InOrder inOrder = inOrder(repositorio, preguntasRepository);

        //validamos que se invoque primero metematicas y despues lenguaje
        inOrder.verify(repositorio).findAll();
        inOrder.verify(preguntasRepository).findPreguntasPorExamenId(5L);
        inOrder.verify(repositorio).findAll();
        inOrder.verify(preguntasRepository).findPreguntasPorExamenId(6L);

    }

    @Test
    void testNumeroInvocaciones() {

        when(repositorio.findAll()).thenReturn(Datos.EXAMENES);
        service.findExamenPorNombreConPreguntas("Matematicas");

        //especificar en el argumento del verify cuantas veces se va a llamar
        verify(preguntasRepository).findPreguntasPorExamenId(5L);
        verify(preguntasRepository, times(1)).findPreguntasPorExamenId(5L);
        verify(preguntasRepository, atLeast(1)).findPreguntasPorExamenId(5L);
        verify(preguntasRepository, atLeastOnce()).findPreguntasPorExamenId(5L);
        verify(preguntasRepository, atMost(1)).findPreguntasPorExamenId(5L);
        verify(preguntasRepository, atMostOnce()).findPreguntasPorExamenId(5L);
    }

    @Test
    void testNumeroInvocaciones2() {

        when(repositorio.findAll()).thenReturn(Datos.EXAMENES);
        service.findExamenPorNombreConPreguntas("Matematicas");

        //especificar en el argumento del verify cuantas veces se va a llamar
       // verify(preguntasRepository).findPreguntasPorExamenId(5L);  falla
        verify(preguntasRepository, times(2)).findPreguntasPorExamenId(5L);
        verify(preguntasRepository, atLeast(2)).findPreguntasPorExamenId(5L);
        verify(preguntasRepository, atLeastOnce()).findPreguntasPorExamenId(5L);
        verify(preguntasRepository, atMost(2)).findPreguntasPorExamenId(5L);
//        verify(preguntasRepository, atMostOnce()).findPreguntasPorExamenId(5L);
    }

    @Test
    void testNumeroInvocaciones3() {
        when(repositorio.findAll()).thenReturn(Collections.emptyList());
        service.findExamenPorNombreConPreguntas("Matematicas");
        verify(preguntasRepository,never()).findPreguntasPorExamenId(5L);
        verifyNoInteractions(preguntasRepository);
        verify(repositorio, times(1)).findAll();
        verify(repositorio, atLeast(1)).findAll();
        verify(repositorio, atLeastOnce()).findAll();
        verify(repositorio, atMost(10)).findAll();
        verify(repositorio, atMostOnce()).findAll();



    }
}