package mx.edu.uacm.userservices.service;

import mx.edu.uacm.userservices.dto.*;
import mx.edu.uacm.userservices.exception.ComensalNoEncontradoException;
import mx.edu.uacm.userservices.exception.CredencialesInvalidasException;
import mx.edu.uacm.userservices.exception.RecuperarCuentaException;
import mx.edu.uacm.userservices.exception.TelefonoRegistradoException;
import mx.edu.uacm.userservices.model.Comensal;
import mx.edu.uacm.userservices.model.Rol;
import mx.edu.uacm.userservices.model.Usuario;
import mx.edu.uacm.userservices.respository.ComensalRepository;
import mx.edu.uacm.userservices.respository.RolRepository;
import mx.edu.uacm.userservices.respository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ComensalService {

    @Autowired
    private  ComensalRepository comensalRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RolRepository rolRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private JwtService jwtService;


    public Comensal executeSaveCustomer(Comensal comensal){

        if(usuarioRepository.existsByCorreo(comensal.getCorreo())){
            throw new RuntimeException("El correo ya está registrado");
        }

        if(comensalRepository.existsByTelefono(comensal.getTelefono())){
            throw new RuntimeException("El teléfono ya está registrado");
        }

        Rol rol = rolRepository.findByNombre("USER")
                .orElseThrow(() ->
                        new RuntimeException("Rol USER no encontrado"));

        comensal.setRol(rol);

        String passwordHash = passwordEncoder.encode(comensal.getContrasenia());
        comensal.setContrasenia(passwordHash);
        return comensalRepository.save(comensal);
    }

    public LoginResponse executeLoginCustomer(LoginRequest loginRequest){

        Usuario usuario = usuarioRepository.findByCorreo(loginRequest.getCorreo())
                .orElseThrow( () -> new CredencialesInvalidasException("Correo o contraseña incorrectos"));

        boolean passwordCorrecto = passwordEncoder.matches(
                loginRequest.getContrasenia(),
                usuario.getContrasenia()
        );

        if (!passwordCorrecto) {
            throw new CredencialesInvalidasException("Correo o contraseña incorrectos");
        }

        if(!usuario.getRol().getNombre().equals("USER")){
            throw new CredencialesInvalidasException(
                    "Correo o contraseña incorrectos"
            );
        }

        Comensal comensal = (Comensal) usuario;

        String token = jwtService.gererateToken(comensal.getId(), comensal.getCorreo(), comensal.getRol().getNombre());

        return new LoginResponse(
                comensal.getId(),
                comensal.getNombre(),
                comensal.getApePaterno(),
                comensal.getApeMaterno(),
                comensal.getCorreo(),
                comensal.getTelefono(),
                comensal.getRol().getNombre(),
                token
        );
    }

    public ComensalResponse executeGetCustomerProfile(String correo){
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() ->
                        new RuntimeException("Comensal no encontrado")
                );

        if (!usuario.getRol().getNombre().equals("USER")) {
            throw new ComensalNoEncontradoException( "Comensal no encontrado" );
        }

        Comensal comensal = (Comensal) usuario;

        return new ComensalResponse(
                comensal.getId(),
                comensal.getNombre(),
                comensal.getApePaterno(),
                comensal.getApeMaterno(),
                comensal.getCorreo(),
                comensal.getTelefono(),
                comensal.getRol().getNombre()
        );
    }

    public ComensalResponse executeUpdateCustomer(String correo, ActualizarPerfilRequest actualizarPerfilRequest){

        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() ->
                        new ComensalNoEncontradoException(
                                "Comensal no encontrado"
                        )
                );

        if (!usuario.getRol().getNombre().equals("USER")) {
            throw new ComensalNoEncontradoException(
                    "Comensal no encontrado"
            );
        }

        Comensal comensal = (Comensal) usuario;

        boolean telefonoRegistrado =
                comensalRepository.existsByTelefonoAndIdNot(
                        actualizarPerfilRequest.getTelefono(),
                        comensal.getId()
                );

        if(telefonoRegistrado){
            throw new TelefonoRegistradoException("Teléfono ya se encuentra registrado");
        }

        comensal.setNombre(actualizarPerfilRequest.getNombre());
        comensal.setTelefono(actualizarPerfilRequest.getTelefono());

        comensalRepository.save(comensal);

        return new ComensalResponse(
                comensal.getId(),
                comensal.getNombre(),
                comensal.getApePaterno(),
                comensal.getApeMaterno(),
                comensal.getCorreo(),
                comensal.getTelefono(),
                comensal.getRol().getNombre()

        );
    }

    public RecuperarCuentaResponse executeRecoverCustomer( RecuperarCuentaRequest recuperarCuentaRequest ){
        Comensal comensal = comensalRepository.findByCorreoAndTelefono(recuperarCuentaRequest.getCorreo(), recuperarCuentaRequest.getTelefono())
                .orElseThrow( () -> new RecuperarCuentaException("Los Datos proporcionados no coinciden.") );

        String recoveryToken = jwtService.generateRecoveryToken( comensal.getId() , comensal.getCorreo());

        return new RecuperarCuentaResponse(
                "Datos validados correctamente",
                recoveryToken
        );

    }

    public void executeResetCustomerPassword(NuevaContraseniaRequest nuevaContraseniaRequest){
        String token = nuevaContraseniaRequest.getRecoveryToken();

        if (!jwtService.isRecoveryTokenValid(token)) {
            throw new RecuperarCuentaException(
                    "Token de recuperación inválido o expirado"
            );
        }

        String correo = jwtService.extractCorreo(token);

        Comensal comensal =
                comensalRepository.findByCorreo(correo)
                        .orElseThrow(() ->
                                new RecuperarCuentaException( "No fue posible recuperar la cuenta" )
                        );

        String passwordHash = passwordEncoder.encode( nuevaContraseniaRequest.getNuevaContrasenia() );

        comensal.setContrasenia(passwordHash);

        comensalRepository.save(comensal);
    }

}
