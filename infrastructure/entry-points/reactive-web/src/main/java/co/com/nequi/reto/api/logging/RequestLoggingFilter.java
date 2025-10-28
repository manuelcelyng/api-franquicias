package co.com.nequi.reto.api.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Filtro transversal para logging de solicitudes HTTP en WebFlux.
 *
 * Qué registra:
 * - [REQ-IN]: cuando se recibe el request (método, path, correlationId)
 * - [REQ-OUT]: cuando se termina de procesar (status HTTP y duración en ms)
 * - [REQ-ERROR]: si ocurre un error no controlado durante el procesamiento
 *
 * CorrelationId:
 * - Lee el header X-Correlation-Id si viene del cliente; si no, genera uno.
 * - Devuelve siempre el header X-Correlation-Id en la respuesta para trazabilidad end-to-end.
 */
@Slf4j
@Component
public class RequestLoggingFilter implements WebFilter {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        final long start = System.currentTimeMillis();
        final HttpMethod httpMethod = exchange.getRequest().getMethod();
        final String method = httpMethod != null ? httpMethod.name() : "UNKNOWN";
        final String path = exchange.getRequest().getURI().getPath();
        String correlationId = exchange.getRequest().getHeaders().getFirst(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }
        final String cid = correlationId;

        // Propaga el correlationId en la respuesta para el cliente
        final ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().set(CORRELATION_ID_HEADER, cid);

        log.info("[REQ-IN] method={} path={} correlationId={}", method, path, cid);

        return chain.filter(exchange)
                // Propaga el CID en el Reactor Context para que el puente Reactor→MDC lo inyecte en todos los logs
                .contextWrite(ctx -> ctx.put("cid", cid))
                .doOnSuccess(ignored -> {
                    Integer status = response.getStatusCode() != null ? response.getStatusCode().value() : null;
                    long duration = System.currentTimeMillis() - start;
                    log.info("[REQ-OUT] method={} path={} status={} durationMs={} correlationId={}",
                            method, path, status, duration, cid);
                })
                .doOnError(ex -> {
                    long duration = System.currentTimeMillis() - start;
                    log.error("[REQ-ERROR] method={} path={} durationMs={} correlationId={} exType={} message={}",
                            method, path, duration, cid, ex.getClass().getSimpleName(), ex.getMessage(), ex);
                });
    }
}
