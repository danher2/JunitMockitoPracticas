package org.dhernandez.test.springboot.app.repositories;

import org.dhernandez.test.springboot.app.models.Banco;
import org.dhernandez.test.springboot.app.models.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BancoRepository  extends JpaRepository<Banco,Long> {

//    List<Banco> findAll();
//    Banco findById(Long id);
//    void update(Banco banco);


}
