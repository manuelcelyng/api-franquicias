# Reto Nequi — Microservicio (Spring WebFlux + Clean Architecture)

Servicio para gestionar franquicias, sucursales y productos con enfoque reactivo (Spring WebFlux) y arquitectura limpia. Incluye health checks, validaciones con Jakarta, manejo global de errores, Dockerfile listo para producción y Terraform por etapas para desplegar en AWS (ECS Fargate + ALB + opcional API Gateway).


## Índice
- Descripción y arquitectura
- Requisitos
- Cómo ejecutar en local
- Variables de entorno (RDS y app)
- Endpoints y contratos
- Manejo de errores (payload)
- Observabilidad y salud
- Docker (build y run)
- Despliegue en AWS (Terraform)
- Notas de seguridad y buenas prácticas


## Descripción y arquitectura
Este proyecto sigue Clean Architecture separando dominio, casos de uso, infraestructura y la aplicación.

- Domain: modelos y contratos de repositorios.
- Usecases: orquesta reglas de negocio (Flujos reactivos Mono/Flux).
- Infrastructure: entry points (WebFlux) y driven adapters (R2DBC PostgreSQL, etc.).
- Application: arranque Spring Boot y configuración.

Estructura completa del repo en el archivo PROJECT STRUCTURE (ver en este README o en el IDE). El enrutamiento es funcional (RouterFunction + HandlerFunction) y los handlers SIEMPRE devuelven Mono<ServerResponse> con el cuerpo potencialmente Flux<T> para streaming.


## Requisitos
- Java 21
- Gradle (wrapper incluido)
- PostgreSQL/Aurora accesible si ejecutas contra RDS real
- Docker (opcional) para empaquetar y ejecutar contenedor
- Terraform + AWS CLI (solo para despliegue, ver carpeta deployment/terraform)


## Cómo ejecutar en local
1) Variables de entorno mínimas (si usas la BD real):
   - RDS_HOST, RDS_PORT (5432 por defecto), RDS_DATABASE, RDS_SCHEMA
   - RDS_USER, RDS_PASSWORD
   - SERVER_PORT (opcional, por defecto 8080)

2) Ejecutar:
   - Linux/macOS: ./gradlew bootRun
   - Windows: gradlew.bat bootRun

3) Probar health:
   - curl http://localhost:8080/api/v1/check → "I'm alive!"

Nota: La app usa R2DBC para la capa reactiva y Liquibase para el esquema (config en application.yaml). Si deseas levantar con H2 local para pruebas, agrega un perfil específico o variables según tu entorno.


## Variables de entorno (aplicación)
Tomadas de applications/app-service/src/main/resources/application.yaml
- SERVER_PORT: puerto HTTP (default 8080)
- RDS_HOST, RDS_PORT: host/puerto de DB
- RDS_DATABASE, RDS_SCHEMA
- RDS_USER, RDS_PASSWORD: credenciales (en AWS se inyectan desde Secrets Manager)

Liquibase usa JDBC (con sslmode=require si aplica), y los adapters R2DBC leen del mismo set de variables.


## Endpoints y contratos
Base path: /api/v1

1) POST /franchise
- Body: { "name": "BrandX" }
- Respuestas:
  - 200 OK: { "id": 10, "name": "BrandX" }
  - 400 VALIDATION_ERROR si name vacío

2) PATCH /franchise/{franchiseId}
- Body: { "name": "NuevoNombre" } (parcial; solo actualiza si no viene en blanco)
- Respuestas:
  - 200 OK: { "id": 10, "name": "NuevoNombre" }
  - 400 FRN_003 si la franquicia no existe
  - 400 INVALID_ARGUMENT si franchiseId no es numérico

3) POST /franchise/{id}/branch
- Body: { "name": "Sucursal Centro" }
- Respuestas:
  - 200 OK: { "id": 5, "name": "Sucursal Centro", "franchiseId": 10 }
  - 400 BRN_002 si ya existe sucursal con ese nombre en la franquicia
  - 400 INVALID_ARGUMENT si id no es numérico

4) PATCH /branch/{branchId}/franchise/{franchiseId}
- Body: { "name": "Sucursal Renombrada" } (solo campos no vacíos)
- Respuestas:
  - 200 OK: { "id": 5, "name": "Sucursal Renombrada", "franchiseId": 10 }
  - 400 BRN_003 si la sucursal no existe
  - 400 INVALID_ARGUMENT si los path variables no son numéricos

5) POST /branch/{id}/product
- Body: { "name": "Producto A", "stock": 100 }
- Respuestas:
  - 200 OK: { "id": 20, "name": "Producto A", "stock": 100, "branchId": 5 }
  - 400 PRD_002 si el producto ya existe en la sucursal
  - 400 INVALID_ARGUMENT si id no es numérico

6) DELETE /branch/{branchId}/product/{productId}
- Respuestas:
  - 204 No Content
  - 400 BRN_003 o PRD_003 según corresponda
  - 400 INVALID_ARGUMENT si ids no son numéricos

7) PATCH /product/{productId}/branch/{branchId}
- Body: { "name": "Producto X", "stock": 50 } (parcial; solo actualiza campos no nulos/blank)
- Respuestas:
  - 200 OK: { "id": 20, "name": "Producto X", "stock": 50, "branchId": 5 }
  - 400 BRN_003 si la sucursal no existe, o PRD_003 si el producto no existe en esa sucursal
  - 400 INVALID_ARGUMENT si ids no son numéricos

8) GET /product/top?franchiseId={id}
- Respuesta 200 OK: array de objetos ProductTopStock
  - [{ "productName": "ProdA", "branchName": "Sucursal 1", "stock": 200 }, ...]
- Errores:
  - 400 INVALID_ARGUMENT si falta franchiseId o no es numérico
- Streaming: por defecto se serializa como array JSON. Si se requiere NDJSON (stream real), se puede ajustar el content-type a application/x-ndjson en el handler.

9) GET /check
- Respuesta 200 OK: "I'm alive!" (usada por el ALB y para liveness)

Observación: Los contratos de request usan DTOs con validación Jakarta (@NotBlank, @Min). El handler valida antes de invocar casos de uso.


## Manejo de errores (payload)
El manejador global (GlobalErrorHandlerConfig) traduce excepciones a un JSON consistente:
- VALIDATION_ERROR (400): errores de constraint con details por campo
- INVALID_ARGUMENT (400): cuando faltan/malforman parámetros de ruta o query, o el body está vacío
- BusinessException (400): códigos del dominio, por ejemplo
  - FRN_002, FRN_003, BRN_002, BRN_003, PRD_002, PRD_003
- INTERNAL_ERROR (500): fallos inesperados

Ejemplo de payload:
{
  "code": "VALIDATION_ERROR",
  "message": "Datos de entrada inválidos",
  "status": 400,
  "path": "/api/v1/franchise",
  "details": [{"field":"name","message":"must not be blank"}],
  "correlationId": "..." // si viene header X-Correlation-Id
}


## Observabilidad y salud
- Health (simple): GET /api/v1/check devuelve 200 con texto plano
- Actuator: /actuator/health (expuesto para probes), /actuator/prometheus (si se habilita en config)
- Seguridad de cabeceras: el entry point añade cabeceras como Strict-Transport-Security, X-Content-Type-Options, CSP, Referrer-Policy (ver tests ConfigTest)


## Docker
- Build: docker build -t reto-nequi:local -f deployment/Dockerfile .
- Run: docker run --rm -p 8080:8080 \
    -e RDS_HOST=... -e RDS_DATABASE=... -e RDS_SCHEMA=public \
    -e RDS_USER=... -e RDS_PASSWORD=... \
    reto-nequi:local

La imagen usa Java 21 JRE, opciones de memoria con MaxRAMPercentage y usuario no root.


## Despliegue en AWS (Terraform)
La infraestructura como código está en deployment/terraform (por etapas):
- 01-network: Security Groups
- 02-ecr: Repositorio ECR (construcción y push opcional)
- 03-ecs-alb: ECS Fargate + ALB
- 04-apigw: API Gateway (opcional)

Sigue la guía detallada en deployment/terraform/USAGE.md y README.md de esa carpeta. Incluye comandos exactos, variables requeridas y troubleshooting (incluyendo “exec format error” por arquitectura de imagen).


## Notas de seguridad y buenas prácticas
- No enviar secretos en URL o en env_vars. Usa Secrets Manager para RDS_USER/RDS_PASSWORD.
- Autorización por objeto: validar acceso por id (IDOR/BOLA) a nivel de casos de uso si se incorporan usuarios/tenancy.
- HTTPS recomendado en ALB (requiere certificate_arn en la etapa 03).
- Logs de acceso y app en CloudWatch (grupo /ecs/reto-nequi por defecto en despliegue ECS).


## Tests
- Ejecutar: ./gradlew test
- Hay pruebas unitarias de casos de uso e integración WebFlux de endpoints y manejo de errores.

---

### Anexo: Arquitectura base
![Clean Architecture](https://miro.medium.com/max/1400/1*ZdlHz8B0-qu9Y-QO3AXR_w.png)
