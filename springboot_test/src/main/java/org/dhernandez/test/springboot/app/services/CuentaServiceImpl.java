package org.dhernandez.test.springboot.app.services;

import org.dhernandez.test.springboot.app.models.Banco;
import org.dhernandez.test.springboot.app.models.Cuenta;
import org.dhernandez.test.springboot.app.repositories.BancoRepository;
import org.dhernandez.test.springboot.app.repositories.CuentaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


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
    public Cuenta findById(Long id) {
        return cuentaRepository.findById(id);
    }

    @Override
    public int revisarTotalTransferencias(Long bancoId) {

        Banco banco = bancoRepository.findById(bancoId);
        return banco.getTotalTransferencia();
    }

    @Override
    public BigDecimal revisarSaldo(Long cuentaId) {
        Cuenta cuenta = cuentaRepository.findById(cuentaId);

        return cuenta.getSaldo();
    }

    @Override
    public void transferir(Long numCuentaOrigen, Long numCuentaDestino, BigDecimal monto,
                           Long bancoId) {


        //despues realiza ahora si la transferencia
        Cuenta cuentaOrigen = cuentaRepository.findById(numCuentaOrigen);
        cuentaOrigen.debito(monto);
        cuentaRepository.update(cuentaOrigen);

        Cuenta cuentaDestino = cuentaRepository.findById(numCuentaDestino);
        cuentaDestino.credito(monto);
        cuentaRepository.update(cuentaDestino);

        //Lo primero que hace es actualizar el  numero de transferencias
        Banco banco = bancoRepository.findById(bancoId);
        int totalTransferencia = banco.getTotalTransferencia();
        banco.setTotalTransferencia(++totalTransferencia); // porque el incremento?
        bancoRepository.update(banco);

    }
}
