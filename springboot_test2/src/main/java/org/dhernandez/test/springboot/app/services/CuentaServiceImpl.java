package org.dhernandez.test.springboot.app.services;

import org.dhernandez.test.springboot.app.models.Banco;
import org.dhernandez.test.springboot.app.models.Cuenta;
import org.dhernandez.test.springboot.app.repositories.BancoRepository;
import org.dhernandez.test.springboot.app.repositories.CuentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;


@Service
public class CuentaServiceImpl implements CuentaService {


    private CuentaRepository cuentaRepository;

    private BancoRepository bancoRepository;

    //constructor para que despues se pueda inyectar via constructor por mockito
    public CuentaServiceImpl(CuentaRepository cuentaRepository, BancoRepository bancoRepository) {
        this.cuentaRepository = cuentaRepository;
        this.bancoRepository = bancoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Cuenta findById(Long id) {
        return cuentaRepository.findById(id).orElseThrow(null);
    }

    @Override
    @Transactional(readOnly = true)
    public int revisarTotalTransferencias(Long bancoId) {

        Banco banco = bancoRepository.findById(bancoId).orElseThrow(null);
        return banco.getTotalTransferencia();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal revisarSaldo(Long cuentaId) {
        Cuenta cuenta = cuentaRepository.findById(cuentaId).orElseThrow(null);

        return cuenta.getSaldo();
    }

    @Override
    @Transactional
    public void transferir(Long numCuentaOrigen, Long numCuentaDestino, BigDecimal monto,
                           Long bancoId) {


        //despues realiza ahora si la transferencia
        Cuenta cuentaOrigen = cuentaRepository.findById(numCuentaOrigen).orElseThrow(null);
        cuentaOrigen.debito(monto);
        cuentaRepository.save(cuentaOrigen);

        Cuenta cuentaDestino = cuentaRepository.findById(numCuentaDestino).orElseThrow(null);
        cuentaDestino.credito(monto);
        cuentaRepository.save(cuentaDestino);

        //Lo primero que hace es actualizar el  numero de transferencias
        Banco banco = bancoRepository.findById(bancoId).orElseThrow(null);
        int totalTransferencia = banco.getTotalTransferencia();
        banco.setTotalTransferencia(++totalTransferencia); // porque el incremento?
        bancoRepository.save(banco);

    }

    @Override
    @Transactional(readOnly = true)
    public List<Cuenta> findAll() {
        return cuentaRepository.findAll();
    }

    @Override
    @Transactional
    public Cuenta save(Cuenta cuenta) {
        return cuentaRepository.save(cuenta);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        cuentaRepository.deleteById(id);
    }
}
