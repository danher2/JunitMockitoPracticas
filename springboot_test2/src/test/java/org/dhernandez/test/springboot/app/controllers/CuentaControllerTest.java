package org.dhernandez.test.springboot.app.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dhernandez.test.springboot.app.Datos;
import org.dhernandez.test.springboot.app.models.Cuenta;
import org.dhernandez.test.springboot.app.models.TransaccionDto;
import org.dhernandez.test.springboot.app.services.CuentaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CuentaController.class)// test MVC para cuentaController trabajara con el controlador real
class CuentaControllerTest {


    //este es para simular todos los metodos mvc del controlador
    @Autowired
    private MockMvc mvc;  //viene todo configurado para contexto de prueva pero es falso

    @MockBean
    private CuentaService cuentaService;


    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testDetalle() throws Exception {
        //Given  contexto de prueba
        when(cuentaService.findById(1L)).thenReturn(Datos.crearCuenta001().orElseThrow(null));

        //When - cuando haga la llamada simulada al endpoint
        mvc.perform(MockMvcRequestBuilders.get("/api/cuentas/1").contentType(MediaType.APPLICATION_JSON))
        //Then esperamos un resultado que sea correcto
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.persona").value("Andres"))
                .andExpect(jsonPath("$.saldo").value("1000"));

                //comprobando que se invoque el findById
                verify(cuentaService).findById(1L);
    }

    @Test
    void testTransferir() throws Exception {

        //Given
        TransaccionDto dto = new TransaccionDto();
        dto.setCuentaOrigenId(1L);
        dto.setCuentaDestinoId(2L);
        dto.setMonto(new BigDecimal("100"));
        dto.setBancoId(1L);

        System.out.println(objectMapper.writeValueAsString(dto));

        Map<String,Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status","OK");
        response.put("mensaje", "Transferencia realizada con exito!");
        response.put("transaccion",dto);

        System.out.println(objectMapper.writeValueAsString(response));

        //When
        mvc.perform(MockMvcRequestBuilders.post("/api/cuentas/transferencia")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        //then

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.date").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.mensaje").value("Transferencia realizada con exito!"))
                .andExpect(jsonPath("$.transaccion.cuentaOrigenId").value(dto.getCuentaOrigenId()))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void testListar() throws Exception {
        //Given
        List<Cuenta> cuentas = Arrays.asList(Datos.crearCuenta001().get(),Datos.crearCuenta002().get());

        when(cuentaService.findAll()).thenReturn(cuentas);


        //When
        mvc.perform(MockMvcRequestBuilders.get("/api/cuentas").contentType(MediaType.APPLICATION_JSON))

        //then
        .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].persona").value("Andres"))
                .andExpect(jsonPath("$[1].persona").value("Jhon"))
                .andExpect(jsonPath("$[0].saldo").value("1000"))
                .andExpect(jsonPath("$[1].saldo").value("2000"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().json(objectMapper.writeValueAsString(cuentas)));

                verify(cuentaService).findAll();

    }


    @Test
    void guardar()  throws Exception{

        //Given
        Cuenta cuenta = new Cuenta(null, "Pepe", new BigDecimal("3000"));
        when(cuentaService.save(any())).then(invocation -> {
            Cuenta c = invocation.getArgument(0);
            c.setId(3L);
            return c;
        });

        //When
        mvc.perform(MockMvcRequestBuilders.post("/api/cuentas").contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(cuenta)))

                //Then
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.persona", is("Pepe")))
                .andExpect(jsonPath("$.saldo",is(3000)));

        verify(cuentaService).save(any());


    }


}