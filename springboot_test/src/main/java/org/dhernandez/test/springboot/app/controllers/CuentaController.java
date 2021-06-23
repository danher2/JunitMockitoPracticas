package org.dhernandez.test.springboot.app.controllers;


import org.dhernandez.test.springboot.app.models.Cuenta;
import org.dhernandez.test.springboot.app.models.TransaccionDto;
import org.dhernandez.test.springboot.app.services.CuentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/Cuentas")
public class CuentaController {

    @Autowired
    private CuentaService cuentaService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Cuenta detalle(@PathVariable(name = "id") Long id){
        return cuentaService.findById(id);
    }

    @PostMapping("/transferencia")
    public ResponseEntity<?> trasferir(@RequestBody TransaccionDto dto){
        cuentaService.transferir(dto.getCuentaOrigenId(), dto.getCuentaDestinoId(), dto.getMonto(), dto.getBancoId());
        Map<String,Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status","OK");
        response.put("mensaje", "Transferencia realizada con exito!");
        response.put("transaccion",dto);

        return ResponseEntity.ok(response);



    }


}
