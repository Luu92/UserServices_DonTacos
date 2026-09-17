# Don Taco's — User Services

Microservicio encargado de la gestión de usuarios de **Don Taco's**.

Este servicio forma parte de la arquitectura de microservicios desarrollada para el sistema de gestión de pedidos de la taquería. Su responsabilidad se limita a la lógica de negocio relacionada con los **comensales y administradores de la taquería**.

## Responsabilidad del microservicio

`userServices` administra la información y las operaciones relacionadas con los usuarios del sistema.

Actualmente contempla dos tipos de usuario:

* **Comensal (`USER`)**: usuario que utiliza la aplicación móvil para interactuar con los servicios de la taquería.
* **Administrador (`ADMIN`)**: usuario perteneciente a la taquería que utiliza las herramientas administrativas del sistema.

Este microservicio **no administra pedidos, alimentos, categorías, promociones ni otras funcionalidades pertenecientes a los demás microservicios**.

La comunicación con esos dominios deberá realizarse mediante los mecanismos de comunicación definidos entre los diferentes servicios.

## Tecnologías

El microservicio fue desarrollado utilizando:

* Java 17
* Spring Boot
* Spring Web MVC
* Spring Data JPA
* Spring Security
* Bean Validation
* PostgreSQL
* BCrypt
* JSON Web Token (JWT)
* JJWT
* Maven
* Lombok

## Arquitectura

El proyecto mantiene una separación de responsabilidades mediante las siguientes capas:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL
```

Además, Spring Security y el filtro JWT se encargan de interceptar las peticiones que requieren autenticación.

```text
Cliente
   ↓
HTTP Request
   ↓
Spring Security
   ↓
JwtAuthenticationFilter
   ↓
Controller
   ↓
Service
   ↓
Repository
   ↓
PostgreSQL
```

## Modelo de usuarios

Se utiliza herencia JPA mediante la estrategia:

```java
@Inheritance(strategy = InheritanceType.JOINED)
```

La entidad `Usuario` contiene la información común de los usuarios.

De ella se derivan:

```text
Usuario
├── Comensal
└── Administrador
```

Cada usuario tiene asociado un rol mediante una relación `ManyToOne`.

Los roles definidos actualmente son:

```text
USER
ADMIN
```

Durante el inicio de la aplicación, los roles pueden ser creados automáticamente si todavía no existen en la base de datos.

## Registro de comensales

El registro permite crear nuevos comensales en el sistema.

```http
POST /comensal/registrar
```

Durante el registro, el backend realiza, entre otras, las siguientes operaciones:

* Validación de los datos recibidos.
* Validación del formato del correo electrónico.
* Validación del número telefónico.
* Verificación de correo electrónico duplicado.
* Verificación de teléfono duplicado.
* Asignación automática del rol `USER`.
* Cifrado de contraseña utilizando BCrypt.
* Persistencia del comensal en PostgreSQL.

El cliente no selecciona el rol durante el registro.

## Autenticación

El inicio de sesión del comensal se realiza mediante:

```http
POST /comensal/auth
```

Ejemplo de petición:

```json
{
  "correo": "usuario@correo.com",
  "contrasenia": "password"
}
```

El backend busca al usuario mediante su correo y verifica la contraseña utilizando BCrypt.

También comprueba que el usuario corresponda al rol permitido para el flujo de autenticación del comensal.

Cuando las credenciales son correctas se genera un **JSON Web Token (JWT)**.

La respuesta contiene información básica del comensal junto con su token de acceso.

Ejemplo conceptual:

```json
{
  "id": 1,
  "nombre": "Nombre",
  "apePaterno": "Apellido",
  "apeMaterno": "Apellido",
  "correo": "usuario@correo.com",
  "telefono": "5512345678",
  "rol": "USER",
  "token": "eyJ..."
}
```

## Seguridad de contraseñas

Las contraseñas no se almacenan en texto plano.

Durante el registro se utiliza:

```java
passwordEncoder.encode(contrasenia);
```

Durante el inicio de sesión:

```java
passwordEncoder.matches(
    contraseniaRecibida,
    contraseniaAlmacenada
);
```

Por lo tanto, el sistema compara la contraseña proporcionada por el usuario contra el hash almacenado en la base de datos.

## JWT

Después de una autenticación correcta, el microservicio genera un JWT firmado.

Actualmente el token contiene información como:

```json
{
  "sub": "usuario@correo.com",
  "id": 1,
  "rol": "USER",
  "iat": 0,
  "exp": 0
}
```

La contraseña **nunca se almacena dentro del JWT**.

El token tiene una vigencia configurada actualmente de:

```properties
jwt.expiration=3600000
```

equivalente a **1 hora**.

El cliente deberá enviar el token en los endpoints protegidos utilizando:

```http
Authorization: Bearer <token>
```

## Filtro JWT

`JwtAuthenticationFilter` intercepta las peticiones HTTP y verifica la existencia del encabezado:

```http
Authorization: Bearer <token>
```

Cuando el token es válido:

1. Se valida su firma y vigencia.
2. Se obtiene el correo del usuario.
3. Se obtiene su rol.
4. Se crea la autenticación utilizada por Spring Security.
5. La autenticación se almacena en `SecurityContextHolder`.

El rol incluido en el JWT se transforma en una autoridad de Spring Security.

Por ejemplo:

```text
USER
 ↓
ROLE_USER
```

Esto permite utilizar reglas como:

```java
.hasRole("USER")
```

para proteger los recursos.

## Perfil del comensal

El perfil del usuario autenticado puede consultarse mediante:

```http
GET /comensal/perfil
```

Este endpoint requiere un JWT válido correspondiente a un usuario con rol `USER`.

El cliente **no necesita enviar el ID o correo del comensal** para identificar al propietario del perfil.

Spring Security obtiene la identidad desde el JWT y el backend utiliza dicha información para consultar los datos actuales del comensal.

Flujo simplificado:

```text
Flutter
   ↓
GET /comensal/perfil
Authorization: Bearer JWT
   ↓
JwtAuthenticationFilter
   ↓
SecurityContextHolder
   ↓
correo del usuario autenticado
   ↓
ComensalService
   ↓
PostgreSQL
   ↓
Perfil
```

Esto evita depender de un identificador proporcionado manualmente por el cliente para consultar el perfil propio.

## Códigos HTTP

El microservicio utiliza códigos HTTP de acuerdo con el resultado de las operaciones.

Entre los principales se encuentran:

```text
200 OK
Petición procesada correctamente.

201 Created
Comensal registrado correctamente.

400 Bad Request
Datos enviados incorrectos o errores de validación.

401 Unauthorized
Credenciales incorrectas, token inexistente o token inválido.

403 Forbidden
Usuario autenticado sin autorización suficiente para acceder al recurso.

404 Not Found
Recurso solicitado no encontrado.

409 Conflict
Correo o teléfono previamente registrado.
```

## Manejo global de excepciones

Las excepciones de la aplicación se centralizan mediante:

```java
@RestControllerAdvice
```

Esto permite evitar respuestas extensas generadas automáticamente por Spring y entregar al cliente respuestas más claras.

Ejemplo:

```json
{
  "mensaje": "Correo o contraseña incorrectos"
}
```

Las validaciones de los DTO o entidades también son procesadas para devolver mensajes comprensibles al frontend.

## Configuración JWT

La configuración actual utiliza propiedades similares a:

```properties
jwt.secret=CLAVE_SECRETA
jwt.expiration=3600000
```

La clave incluida en el archivo de propiedades se utiliza únicamente durante el desarrollo.

En un ambiente de producción, la clave JWT deberá almacenarse mediante una variable de entorno o un mecanismo seguro de gestión de secretos y **no deberá publicarse en el repositorio**.

## Base de datos

El microservicio utiliza PostgreSQL.

Configuración general:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/***
spring.datasource.username=postgres
spring.datasource.password=********
```

Las credenciales reales de la base de datos no deberán publicarse en el repositorio.

Durante el desarrollo puede utilizarse Hibernate para administrar el esquema de la base de datos.

## Alcance

Este microservicio se limita al dominio de usuarios de Don Taco's Food.

### Pertenece a `userServices`

```text
Comensales
Administradores
Roles
Registro de usuarios
Autenticación
Autorización
Perfil
Seguridad relacionada con usuarios
```
## Estado actual

Actualmente se encuentran implementadas y probadas las siguientes funcionalidades:

```text
Registro de comensales                ✓
Validaciones de correo y teléfono     ✓
Control de datos duplicados           ✓
Asignación automática de rol USER     ✓
Contraseñas con BCrypt                ✓
Login de comensales                   ✓
Generación de JWT                     ✓
Validación de JWT                     ✓
Autenticación con Spring Security     ✓
Autorización mediante ROLE_USER       ✓
Consulta del perfil autenticado       ✓
Manejo global de excepciones          ✓
```

Se realizaron pruebas para verificar, entre otros escenarios:

* Inicio de sesión con credenciales correctas.
* Inicio de sesión con correo incorrecto.
* Inicio de sesión con contraseña incorrecta.
* Intento de autenticación con un rol no permitido.
* Acceso a recursos sin JWT.
* Acceso utilizando un JWT válido.
* Rechazo de JWT alterados.
* Acceso a recursos según el rol del usuario.
* Consulta del perfil correspondiente al usuario autenticado.


**Don Taco's**

Microservicio `userServices` — Gestión de usuarios, autenticación y autorización.
