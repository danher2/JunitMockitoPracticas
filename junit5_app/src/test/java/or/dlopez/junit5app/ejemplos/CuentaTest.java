package or.dlopez.junit5app.ejemplos;

import or.dlopez.junit5app.ejemplos.exceptions.DineroInsuficienteException;
import or.dlopez.junit5app.ejemplos.models.Banco;
import or.dlopez.junit5app.ejemplos.models.Cuenta;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CuentaTest {

    Cuenta cuenta;
    private TestInfo testInfo;
    private TestReporter testReporter;


    @BeforeEach
    void initMethodTest(TestInfo testInfo, TestReporter testReporter){
        System.out.println("iniciando el metodo");
        this.cuenta = cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));
        this.testInfo=testInfo;
        this.testReporter=testReporter;

        testReporter.publishEntry("ejecutando " + testInfo.getDisplayName() + " " + testInfo.getTestMethod().orElse(null).getName()
                + " con las etiquetas " + testInfo.getTags());
    }

    @AfterEach
    void tearDown() {
        System.out.println("finalizando metodo de prueba");
    }


    @BeforeAll
    static void beforeAll() {
        System.out.println("inicializando el test");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("finalizando el test");
    }

    static List<String> montoList(){

        return Arrays.asList("100","200","300","500","700","1000.12345");
    }


    @Tag("cuenta")
    @Nested //clase anidada
    @DisplayName("probando atributos de la cuenta corriente")
    class CuentaTestNombreSaldo{

        @Test
        @DisplayName("el nombre")
        void testNombreCuenta() {

            testReporter.publishEntry(testInfo.getTags().toString());
            if (testInfo.getTags().contains("cuenta")){
                testReporter.publishEntry("hacer algo con la etiqueta cuenta");
            }
            //        cuenta.setPersona("Andres");
            String esperado = "Andres";
            String real = cuenta.getPersona();
            assertNotNull(real, () -> "El objeto o la cuenta no puede ser nula");
            Assertions.assertEquals(esperado, real,() -> "el nombre de la cuenta no es el que se esperaba " + esperado + " sin embargo" +
                    " se recibio: " + real );
            assertTrue(real.equals("Andres"), () -> "nombre cuenta esperado debe ser igual a la real");
        }

        @Test
        @DisplayName("el saldo, que no sea null, mayor que cero, valor esperado")
        void testSaldoCuenta() {

            assertNotNull(cuenta.getSaldo());
            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        //test driven development


        @Test
        @DisplayName("Testeando referencias o instancias que sean iguales con el metodo equals")
        void testReferenciaCuenta() {
            cuenta = new Cuenta("John Doe", new BigDecimal("8900.9997"));
            Cuenta cuenta2 = new Cuenta("John Doe", new BigDecimal("8900.9997"));

//        assertNotEquals(cuenta2,cuenta);
            assertEquals(cuenta2, cuenta);


        }

    }

    @Nested
    class CuentaOperacionesTest{

        @Tag("cuenta")
        @Test
        void testDebitoCuenta() {

            cuenta.debito(new BigDecimal(100));
            assertNotNull(cuenta.getSaldo());
            assertEquals(900, cuenta.getSaldo().intValue());
            assertEquals("900.12345", cuenta.getSaldo().toPlainString()); // devuelve el string plano con el valor del saldo
        }

        @Tag("cuenta")
        @Test
        void testCreditoCuenta() {

            cuenta.credito(new BigDecimal(100));
            assertNotNull(cuenta.getSaldo());
            assertEquals(1100, cuenta.getSaldo().intValue());
            assertEquals("1100.12345", cuenta.getSaldo().toPlainString()); // devuelve el string plano con el valor del saldo
        }


        @Tag("cuenta")
        @Tag("banco")
        @Test
        void testTranferirDineroCuentas() {
            Cuenta cuenta1 = new Cuenta("Jhon Doe", new BigDecimal("2500"));
            Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("1500.8989"));

            Banco banco = new Banco();
            banco.setNombre("Banco del Estado");
            banco.transferir(cuenta2, cuenta1, new BigDecimal("500"));
            assertEquals("1000.8989", cuenta2.getSaldo().toPlainString());
            assertEquals("3000", cuenta1.getSaldo().toPlainString());
        }


    }



    @Test
    @Tag("cuenta")
    @Tag("error")
    void TestDineroInsuficienteException() {

        Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
            cuenta.debito(new BigDecimal(1500));
        });

        String actual = exception.getMessage();
        String esperado = "Dinero insuficiente";
        assertEquals(esperado, actual);

    }




    @Test
    @Tag("cuenta")
    @Tag("banco")
//    @Disabled
    @DisplayName("probando relaciones entre las cuentas y el banco con assertAll")
    void testRelacionBancoCuentas() {
//        fail(); //fuerza el fallo del metodo
        Cuenta cuenta1 = new Cuenta("Jhon Doe", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("1500.8989"));

        Banco banco = new Banco();
        banco.addCuenta(cuenta1);
        banco.addCuenta(cuenta2);

        banco.setNombre("Banco del Estado");
        banco.transferir(cuenta2, cuenta1, new BigDecimal("500"));

        assertAll(
                () -> {
                    assertEquals("1000.8989", cuenta2.getSaldo().toPlainString(),() -> "el valor del saldo de la cuenta2 no es el esperado");
                },
                () -> {
                    assertEquals("3000", cuenta1.getSaldo().toPlainString(),
                            () -> "el valor del saldo de la cuenta2 no es el esperado");
                },
                () -> {
                    assertEquals(2, banco.getCuentas().size(),
                            () -> "el banco no tiene el numero de cuentas no son las esperadas");
                },
                () -> {
                    assertEquals("Banco del Estado", cuenta1.getBanco().getNombre());
                },
                () -> {
                    assertEquals("Andres", banco.getCuentas()
                            .stream()
                            .filter(c -> c.getPersona()
                                    .equals("Andres"))
                            .findFirst()
                            .get().getPersona());
                },
                () -> {
                    assertTrue(banco.getCuentas().stream().anyMatch(c -> c.getPersona().equals("Jhon Doe")));
                }
        );


    }

    @Nested
    class SistemaOperativoTest{

        @Test
        @EnabledOnOs(OS.WINDOWS)
        void testSoloWindows() {
        }

        @Test
        @EnabledOnOs({OS.LINUX,OS.MAC})
        void testSoloLinuxMac() {
        }

        @Test
        @DisabledOnOs(OS.WINDOWS)
        void testNoWindows() {
        }

    }


    @Nested
    class JavaVersionTest{

        @Test
        @EnabledOnJre(JRE.JAVA_12)
        void soloJdk8() {
        }

        @Test
        @EnabledOnJre(JRE.JAVA_8)
        void soloJdk18() {
        }

        @Test
        @DisabledOnJre(JRE.JAVA_8)
        void NoJdk8() {
        }

    }

    @Nested
    class SystemPropertiesTest{

        @Test
        void imprimirSystemProperties() {
            Properties properties = System.getProperties();
            properties.forEach((k,v)->{
                System.out.println(k + ":" + v);
            });
        }

        @Test
        @EnabledIfSystemProperty(named ="java.version", matches = "1.8.0_231")
        void testJavaVersion() {

        }

        @Test
        @DisabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
        void testSolo64() {
        }


        @Test
        @EnabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
        void testNoSolo64() {
        }

        @Test
        @EnabledIfSystemProperty(named ="user.name", matches = "Personal")
        void testUserName() {
        }

        @Test
        @EnabledIfSystemProperty(named = "ENV", matches = "dev")
        void testDev() {
        }


    }


    @Nested
    class VariableAmbienteTest{

        @Test
        void imprimirVariablesAmbiente() {
            Map<String,String> getEnv = System.getenv();
            getEnv.forEach((k,v)->{
                System.out.println(k + " = " + v);
            });

        }

        @Test
        @EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches="C:.*jdk1.8.0_231.*")
        void testJavaHome() {
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "NUMBER_OF_PROCESSORS", matches="8")
        void testProcesadores() {
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "ENVIRONMENT", matches="dev")
        void testEnv() {
        }
        @Test
        @DisabledIfEnvironmentVariable(named = "ENVIRONMENT", matches="prod")
        void testEnvProd() {
        }

    }



    @Test
    @DisplayName("testSaldoCuentaDev")
    void testSaldoCuentaDev() {

        boolean esDev = "dev".equals(System.getProperty("ENV"));
        assumeTrue(esDev); //se ejecuta lo de abajo si se ejecuta esta condicion
        assertNotNull(cuenta.getSaldo());
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }


    @Test
    @DisplayName("testSaldoCuentaDev2")
    void testSaldoCuentaDev2() {

        boolean esDev = "dev".equals(System.getProperty("ENV"));
        assumingThat(esDev, ()->{
            assertNotNull(cuenta.getSaldo());
            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);

        });

    }

    @DisplayName("Probando debito cuenta Repetir")
    @RepeatedTest(value=5, name="{displayName} - Repeticion numero {currentRepetition} de {totalRepetitions}")
    void testDebitoCuentaRetetir(RepetitionInfo info) {

        if (info.getCurrentRepetition() ==3){
            System.out.println("estamos en la repeticion " + info.getCurrentRepetition());
        }
        cuenta.debito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.12345", cuenta.getSaldo().toPlainString()); // devuelve el string plano con el valor del saldo
    }


    @Tag("param") // para etiquetar todos los metodos de las clases
    @Nested
    class PruebasParametrizadasTests{
        @ParameterizedTest(name = "numero de la repeticion {index} ejecutando con el valor {0}-{argumentsWithNames}")
        @ValueSource(strings = {"100","200","300","500","700","1000.12345"})
        void testDebitoCuentaValueSource(String monto) {

            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO)>0);

        }



        @ParameterizedTest(name = "numero de la repeticion {index} ejecutando con el valor {0}-{argumentsWithNames}")
        @CsvSource({"1,100","2,200","3,300","4,500","5,700","6,1000.12345"})
        void testDebitoCuentaCsvSource(String index, String monto) {
            System.out.println(index + "->" + monto);
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO)>0);

        }

        @ParameterizedTest(name = "numero de la repeticion {index} ejecutando con el valor {0}-{argumentsWithNames}")
        @CsvFileSource(resources = "/data.csv")
        void testDebitoCuentaCsvFileSource(String monto) {
            System.out.println(monto);
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO)>0);

        }


        @ParameterizedTest(name = "numero de la repeticion {index} ejecutando con el valor {0}-{argumentsWithNames}")
        @CsvSource({"200,100,John,Andres","250,200,Pepe,Pepe","300,300,Maria,maria","510,500,Pepa,Pepa","750,700,Lucas,Luca","1000.12345,1000.12345,Cata,Cata"})
        void testDebitoCuentaCsvSource2(String saldo, String monto, String esperado, String actual) {
            System.out.println(saldo + "->" + monto);
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            cuenta.setPersona( actual);


            assertNotNull(cuenta.getSaldo());
            assertNotNull(cuenta.getPersona());
            assertEquals(esperado,actual);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO)>0);

        }


        @ParameterizedTest(name = "numero de la repeticion {index} ejecutando con el valor {0}-{argumentsWithNames}")
        @CsvFileSource(resources = "/data2.csv")
        void testDebitoCuentaCsvFileSource2(String saldo, String monto, String esperado, String actual) {
            System.out.println(monto);
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            cuenta.setPersona( actual);
            assertNotNull(cuenta.getSaldo());
            assertNotNull(cuenta.getPersona());
            assertEquals(esperado,actual);

            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO)>0);

        }


    }

    @Tag("param")
    @ParameterizedTest(name = "numero de la repeticion {index} ejecutando con el valor {0}-{argumentsWithNames}")
    @MethodSource("montoList")
    void testDebitoCuentaMethodSource(String monto) {
        System.out.println(monto);
        cuenta.debito(new BigDecimal(monto));
        assertNotNull(cuenta.getSaldo());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO)>0);

    }

    @Nested
    @Tag("timeout")
    class EjemplpoTimeOutTest{

        @Test
        @Timeout(1)
        void pruebaTimeOut() throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(100);
        }

        @Test
        @Timeout(value=1000,unit=TimeUnit.MILLISECONDS)
        void pruebaTimeOut2() throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(900);
        }

        @Test
        void testTimeout() {
            assertTimeout(Duration.ofSeconds(5), ()->{
                TimeUnit.MILLISECONDS.sleep(4000);

            });

        }
    }

}