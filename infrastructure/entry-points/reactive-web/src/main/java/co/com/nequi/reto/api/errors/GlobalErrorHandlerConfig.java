package co.com.nequi.reto.api.errors;


import co.com.nequi.reto.usecase.exceptions.BusinessException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@lombok.extern.slf4j.Slf4j
public class GlobalErrorHandlerConfig {

    @Bean
    public DefaultErrorAttributes defaultErrorAttributes() {
        return new DefaultErrorAttributes();
    }

    @Bean
    @Order(-2)
    public ErrorWebExceptionHandler globalExceptionHandler(DefaultErrorAttributes errorAttributes,
                                                           ApplicationContext applicationContext) {
        WebProperties.Resources resources = new WebProperties.Resources();
        return new GlobalErrorWebExceptionHandler(errorAttributes, resources, applicationContext);
    }

    static class GlobalErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

        GlobalErrorWebExceptionHandler(DefaultErrorAttributes g, WebProperties.Resources r, ApplicationContext c) {
            super(g, r, c);
            super.setMessageReaders(ServerCodecConfigurer.create().getReaders());
            super.setMessageWriters(ServerCodecConfigurer.create().getWriters());
        }

        @Override
        protected RouterFunction<ServerResponse> getRoutingFunction(final ErrorAttributes errorAttributes) {
            return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
        }

        private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
            Throwable ex = getError(request);

            String correlationId = request.headers().firstHeader("X-Correlation-Id");

            HttpStatus status;
            ErrorResponse payload;

            // Clasifica y construye payload
            if (ex instanceof ConstraintViolationException cve) { // el validation del jakarta
                status = HttpStatus.BAD_REQUEST;
                List<ErrorDetail> details = cve.getConstraintViolations().stream()
                        .map(v -> new ErrorDetail(v.getPropertyPath().toString(), v.getMessage()))
                        .collect(Collectors.toList());
                payload = ErrorResponse.of(
                        "VALIDATION_ERROR",
                        "Datos de entrada inválidos",
                        status.value(),
                        request.path(),
                        details,
                        correlationId
                );
            }  else if (ex instanceof BusinessException bue) {
                status = HttpStatus.BAD_REQUEST;
                payload = ErrorResponse.of(
                        bue.getCode(),
                        bue.getMessage(),
                        status.value(),
                        request.path(),
                        null,
                        correlationId
                );
            } else if (ex instanceof IllegalArgumentException iae) {
                status = HttpStatus.BAD_REQUEST;
                payload = ErrorResponse.of(
                        "INVALID_ARGUMENT",
                        iae.getMessage(),
                        status.value(),
                        request.path(),
                        null,
                        correlationId
                );
            } else {
                status = HttpStatus.INTERNAL_SERVER_ERROR;
                payload = ErrorResponse.of(
                        "INTERNAL_ERROR",
                        "Ocurrió un error inesperado",
                        status.value(),
                        request.path(),
                        null,
                        correlationId
                );
            }

            // Logging del error con severidad según tipo de status
            if (status.is4xxClientError()) {
                // WARN para errores de validación/negocio/argumentos del cliente
                log.warn("[ERROR-HANDLED] status={} path={} correlationId={} type={} message={}",
                        status.value(), request.path(), correlationId, ex.getClass().getSimpleName(), ex.getMessage());
            } else {
                // ERROR para fallos del servidor
                log.error("[ERROR-HANDLED] status={} path={} correlationId={} type={} message={}",
                        status.value(), request.path(), correlationId, ex.getClass().getSimpleName(), ex.getMessage(), ex);
            }

            return ServerResponse.status(status)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(payload));
        }
    }
}