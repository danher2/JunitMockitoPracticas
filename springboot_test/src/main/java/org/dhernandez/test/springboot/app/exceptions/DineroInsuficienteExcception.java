package org.dhernandez.test.springboot.app.exceptions;


//clase que maneja las excepciones
public class DineroInsuficienteExcception extends RuntimeException {

    //constructor
    public DineroInsuficienteExcception(String message) {
        super(message);
    }
}
