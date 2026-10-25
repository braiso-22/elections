# elections

Aplicación de votaciones sobre elecciones convocadas en el BOE. Es un monolito modular en Kotlin con
DDD y arquitectura hexagonal: un servidor Ktor y una web en React que comparten el contrato HTTP.

## Módulos

| Módulo | Qué contiene |
|--------|--------------|
| `kernel` | Lo compartido por todos los `core`: identificadores (`EleccionId`, `PartidoId`…), categorías de error de dominio, eventos y bus en proceso. |
| `shared` | DTOs de petición y respuesta (Kotlin Multiplatform, JVM + JS). Los usan Ktor y React. |
| `<contexto>/core` | Dominio y casos de uso en Kotlin puro. Solo puede depender de `kernel`. |
| `<contexto>/infrastructure` | Adaptadores: rutas Ktor, repositorios, integraciones externas y módulo de Koin. |
| `server` | Composition root: arranca Ktor, instala plugins y monta los contextos. |
| `app/webApp` | Web en React que consume `shared`. |

En Gradle cada módulo de contexto se llama `:<contexto>-<capa>`, por ejemplo `:votacion-core`.

### Bounded contexts

- **partidos**: catálogo de todos los partidos inscritos, importado del Registro de Partidos Políticos de Interior.
- **elecciones**: elecciones convocadas en el BOE, sus candidaturas y su periodo de votación. Una elección está
  `CONVOCADA` antes del periodo, `ABIERTA` durante y `CERRADA` después. Sus candidaturas se congelan al abrirse.
- **votacion**: un voto por votante y elección, que se puede cambiar mientras la elección esté abierta. Se puede votar
  a una candidatura, en blanco o en contra del sistema. Publica `VotoEmitido` y `VotoCambiado`.
- **resultados**: recuento en tiempo real construido a partir de los eventos de votación.
- **identidad**: quién vota (el proveedor OIDC) y quién administra.

### Reglas de dependencia

- Un `core` no conoce Ktor, Koin ni ningún otro contexto. Lo garantiza Gradle (un `core` solo declara `kernel`) y lo
  comprueba `ArquitecturaTest`.
- Cuando un contexto necesita a otro, define un puerto en su `core` (p. ej. `ConsultaElecciones` en votación) y su
  `infrastructure` lo implementa llamando a los casos de uso del otro contexto.
- Las reacciones a cambios van por eventos de dominio: resultados se suscribe a los de votación.
- Los errores de dominio heredan de una categoría del kernel y `StatusPages` traduce cada categoría a un status:
  `RecursoNoEncontrado` → 404, `OperacionNoPermitida` → 409, `DatoNoValido` → 422 y `AccesoNoPermitido` → 403.

### Adaptadores de momento falsos

| Puerto | Ahora | Después |
|--------|-------|---------|
| Repositorios | En memoria: los datos se pierden al reiniciar | Postgres + Exposed |
| `RegistroDePartidos` | `SampleRegistroDePartidos` | Registro de Partidos de Interior |
| `BoletinOficial` | `SampleBoletinOficial` (referencia `BOE-A-EJEMPLO-1`) | API del BOE (`BoeApiClient` ya existe) |
| Autenticación | Bearer de desarrollo: el token es el id del votante | JWT de Supabase |
| `RegistroDeAdministradores` | Variable de entorno `ELECTIONS_ADMINS` | Tabla propia |

## API

Las rutas marcadas con 🔒 necesitan `Authorization: Bearer <votante>`, y las de `/v1/admin` además que ese votante
sea administrador.

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/v1/partidos?busqueda=` | Catálogo de partidos, filtrable por nombre o siglas |
| GET | `/v1/partidos/{id}` | Un partido |
| GET | `/v1/elecciones` | Elecciones con su estado |
| GET | `/v1/elecciones/{id}` | Una elección con sus candidaturas |
| PUT 🔒 | `/v1/elecciones/{id}/voto` | Emitir o cambiar el voto: `{"opcion":"CANDIDATURA","candidaturaId":"…"}`, `{"opcion":"EN_BLANCO"}` o `{"opcion":"CONTRA_EL_SISTEMA"}` |
| GET 🔒 | `/v1/elecciones/{id}/voto` | Mi voto en esa elección |
| GET | `/v1/elecciones/{id}/resultados` | Recuento, porcentajes y participación |
| POST 🔒 | `/v1/admin/importaciones/registro-partidos` | Sincroniza el catálogo con el registro |
| POST 🔒 | `/v1/admin/importaciones/boe` | Importa una elección: `{"referenciaBoe":"BOE-A-EJEMPLO-1","inicioVotacion":"2027-05-23T07:00:00Z","finVotacion":"2027-05-24T07:00:00Z"}` |
| GET | `/health` | Comprobación de vida |

Los errores responden `{"codigo": "<ErrorDeDominio>", "mensaje": "…"}`.

## Ejecutar

- Servidor: `ELECTIONS_ADMINS=admin ./gradlew :server:run` (puerto en `PORT`, 8080 por defecto)
- Tests: `./gradlew check`
- Docker: `docker compose up --build`
- Web (en `http://localhost:5173`, con la API reenviada al 8080):
  ```shell
  npm install
  npm run start
  ```
