package mx.edu.uacm.userservices.service;

import mx.edu.uacm.userservices.dto.ComensalResponse;
import mx.edu.uacm.userservices.dto.LoginRequest;
import mx.edu.uacm.userservices.dto.LoginResponse;
import mx.edu.uacm.userservices.exception.ComensalNoEncontradoException;
import mx.edu.uacm.userservices.exception.CredencialesInvalidasException;
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


    public Comensal executeSaveComensal(Comensal comensal){

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

}
