package org.dhernandez.test.springboot.app;


import org.dhernandez.test.springboot.app.models.Cuenta;
import org.dhernandez.test.springboot.app.repositories.CuentaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class IntegracionJpaTest {
    @Autowired
    CuentaRepository cuentaRepository;

    @Test
    void testFindById() {
        Optional<Cuenta> cuenta = cuentaRepository.findById(1L);
        assertTrue(cuenta.isPresent());
        assertEquals("Andres",cuenta.orElseThrow(null).getPersona());
    }


    @Test
    void testFindByPersona() {
        Optional<Cuenta> cuenta = cuentaRepository.findByPersona("Andres");
        assertTrue(cuenta.isPresent());
        assertEquals("Andres",cuenta.orElseThrow(null).getPersona());
        assertEquals("1000.00",cuenta.orElseThrow(null).getSaldo().toPlainString());
    }



    @Test
    void testFindByPersonaThrowException() {
        Optional<Cuenta> cuenta = cuentaRepository.findByPersona("Juan");
        assertThrows(NullPointerException.class,()-> cuenta.orElseThrow(null));

        assertFalse(cuenta.isPresent());
    }


    @Test
    void findAll() {

        List<Cuenta> cuentas = cuentaRepository.findAll();
        assertFalse(cuentas.isEmpty());
        assertEquals(2,cuentas.size());
    }

    @Test
    void testSave() {
        //given
        Cuenta cuentaPepe = new Cuenta(null, "Pepe", new BigDecimal("3000"));

        //When
        Cuenta cuenta = cuentaRepository.save(cuentaPepe);
//      Cuenta cuenta = cuentaRepository.findByPersona("Pepe").orElseThrow(null)
//      Cuenta cuenta = cuentaRepository.findById(cuentaSave.getId()).orElseThrow(null);

        //Then
        assertEquals("Pepe", cuenta.getPersona() );
        assertEquals("3000", cuenta.getSaldo().toPlainString());
        assertEquals(3,cuenta.getId());


    }

    @Test
    void testUpdate() {
        //given
        Cuenta cuentaPepe = new Cuenta(null, "Pepe", new BigDecimal("3000"));

        //When
        Cuenta cuenta = cuentaRepository.save(cuentaPepe);
//      Cuenta cuenta = cuentaRepository.findByPersona("Pepe").orElseThrow(null)
//      Cuenta cuenta = cuentaRepository.findById(cuentaSave.getId()).orElseThrow(null);

        //Then
        assertEquals("Pepe", cuenta.getPersona() );
        assertEquals("3000", cuenta.getSaldo().toPlainString());
//        assertEquals(3,cuenta.getId());

        //When
        cuenta.setSaldo(new BigDecimal("3800"));
        Cuenta cuentaActualizada = cuentaRepository.save(cuenta);
        //Then
        assertEquals("Pepe", cuentaActualizada.getPersona() );
        assertEquals("3800", cuentaActualizada.getSaldo().toPlainString());


    }

    @Test
    void testDelete() {

        Cuenta cuenta = cuentaRepository.findById(2L).orElseThrow(null);
        assertEquals("Jhon", cuenta.getPersona());

        cuentaRepository.delete(cuenta);

        assertThrows(NullPointerException.class, ()->{
//            cuentaRepository.findByPersona("Jhon").orElseThrow(null);
            cuentaRepository.findById(2L).orElseThrow(null);

        });
        assertEquals(1,cuentaRepository.findAll().size());
    }
}
