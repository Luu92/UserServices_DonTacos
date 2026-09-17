package mx.edu.uacm.userservices.controller;

import jakarta.validation.Valid;
import mx.edu.uacm.userservices.dto.*;
import mx.edu.uacm.userservices.model.Comensal;
import mx.edu.uacm.userservices.service.ComensalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comensal")
public class ComensalController {

    @Autowired
    private ComensalService comensalService;

    @PostMapping("/registrar")
    public ResponseEntity<RegistroResponse> executeSaveComensal(@Valid @RequestBody Comensal comensal){
        try{
            comensalService.executeSaveComensal(comensal);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new RegistroResponse(
                            "Comensal registrado correctamente"
                    ));
        }catch (RuntimeException e){
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new RegistroResponse(
                            e.getMessage()
                    ));
        }
    }

    @PostMapping("/auth")
    public ResponseEntity<LoginResponse> executeLoginComensal(@RequestBody LoginRequest loginRequest){
            LoginResponse loginResponse = comensalService.executeLoginCustomer(loginRequest);
            return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/perfil")
    public ResponseEntity<ComensalResponse> executeGetProfile( Authentication authentication ) {
        String correo = authentication.getName();
        ComensalResponse comensalResponse = comensalService.executeGetCustomerProfile(correo);
        return ResponseEntity.ok(comensalResponse);
    }


}
