package org.dhernandez.test.springboot.app;

import org.dhernandez.test.springboot.app.exceptions.DineroInsuficienteExcception;
import org.dhernandez.test.springboot.app.models.Banco;
import org.dhernandez.test.springboot.app.models.Cuenta;
import org.dhernandez.test.springboot.app.repositories.BancoRepository;
import org.dhernandez.test.springboot.app.repositories.CuentaRepository;
import org.dhernandez.test.springboot.app.services.CuentaService;
import org.dhernandez.test.springboot.app.services.CuentaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.dhernandez.test.springboot.app.Datos.*;

import java.math.BigDecimal;

@SpringBootTest
class SpringbootTestApplicationTests {


//	@Mock
	@MockBean
	CuentaRepository cuentaRepository;

	//@Mock
	@MockBean
	BancoRepository bancoRepository;

//	@InjectMocks //tiene que ser la clase que implementa el service
//	@Mock
	@Autowired //de forma automatica se inyectan los mockBeans de arriba
	CuentaService  service;


	@BeforeEach
	void setUp() {
//		cuentaRepository = mock(CuentaRepository.class);
//		bancoRepository = mock(BancoRepository.class);
//		//Inyecta los mocks via constructor
//		service = new CuentaServiceImpl(cuentaRepository,bancoRepository);
////		Datos.CUENTA_001.setSaldo(new BigDecimal(("1000")));
//		Datos.CUENTA_002.setSaldo(new BigDecimal(("2000")));
//		Datos.BANCO.setTotalTransferencia(0);
	}

	@Test
	void contextLoads() {
		//given
		when(cuentaRepository.findById(1L)).thenReturn(crearCuenta001());
		when(cuentaRepository.findById(2L)).thenReturn(crearCuenta002());
		when(bancoRepository.findById(1L)).thenReturn(crearBanco());


		BigDecimal saldoOrigen = service.revisarSaldo(1L);
		BigDecimal saldoDestino = service.revisarSaldo(2L);
		assertEquals("1000", saldoOrigen.toPlainString());
		assertEquals("2000", saldoDestino.toPlainString());

		service.transferir(1L,2L,new BigDecimal("100"),1L);
		saldoOrigen = service.revisarSaldo(1L);
		saldoDestino = service.revisarSaldo(2L);
		assertEquals("900",saldoOrigen.toPlainString());
		assertEquals("2100",saldoDestino.toPlainString());

		//Assert par numero de trasferencias
		int total = service.revisarTotalTransferencias(1L);
		assertEquals(1,total);

		//verify verifica cuantas veces se invocaron los metodos de cada mock de cada repositorio
		verify(cuentaRepository, times(3)).findById(1L);
		verify(cuentaRepository, times(3)).findById(2L);
		verify(cuentaRepository, times(2)).save(any(Cuenta.class));

		//cuantas veces se invoca el metodo findbyid en el metodo transferir
		verify(bancoRepository,times(2)).findById(1L);
		verify(bancoRepository).save(any(Banco.class));


		verify(cuentaRepository, times(6)).findById(anyLong());
		verify(cuentaRepository, never()).findAll();

	}


	@Test
	void contextLoads2() {
		//given
		when(cuentaRepository.findById(1L)).thenReturn(crearCuenta001());
		when(cuentaRepository.findById(2L)).thenReturn(crearCuenta002());
		when(bancoRepository.findById(1L)).thenReturn(crearBanco());


		BigDecimal saldoOrigen = service.revisarSaldo(1L);
		BigDecimal saldoDestino = service.revisarSaldo(2L);
		assertEquals("1000", saldoOrigen.toPlainString());
		assertEquals("2000", saldoDestino.toPlainString());

		assertThrows(DineroInsuficienteExcception.class, ()-> {
			//esto causara una excepcion
			service.transferir(1L,2L,new BigDecimal("1200"),1L);

		});

		saldoOrigen = service.revisarSaldo(1L);
		saldoDestino = service.revisarSaldo(2L);
		assertEquals("1000",saldoOrigen.toPlainString());
		assertEquals("2000",saldoDestino.toPlainString());

		//Assert par numero de trasferencias
		int total = service.revisarTotalTransferencias(1L);
		assertEquals(0,total);

		//verify verifica cuantas veces se invocaron los metodos de cada mock de cada repositorio
		verify(cuentaRepository, times(3)).findById(1L);
		verify(cuentaRepository, times(2)).findById(2L);
		verify(cuentaRepository, never()).save(any(Cuenta.class));

		//cuantas veces se invoca el metodo findbyid en el metodo transferir
		verify(bancoRepository,times(1)).findById(1L);
		verify(bancoRepository,never()).save(any(Banco.class));

		verify(cuentaRepository, times(5)).findById(anyLong());
		verify(cuentaRepository, never()).findAll();

	}

	@Test
	void contextLoad3() {
		when(cuentaRepository.findById(1L)).thenReturn(crearCuenta001());
		Cuenta cuenta1 = service.findById(1L);
		Cuenta cuenta2 = service.findById(1L);

			//comparamos si es el mismo objeto
			assertSame(cuenta1,cuenta2);

			assertTrue(cuenta1 == cuenta2);

			assertEquals("Andres", cuenta1.getPersona());
			assertEquals("Andres", cuenta2.getPersona());

			verify(cuentaRepository,times(2)).findById(1L);
	}
}