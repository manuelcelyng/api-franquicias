package co.com.nequi.reto.api.logging;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Operators;

/**
 * Registers a global Reactor operator that lifts Reactor Context values into MDC
 * for all reactive pipelines. This enables %X{cid} to appear in logs across
 * thread changes without passing the value explicitly.
 */
@Configuration
public class ReactorMdcConfig {

    private static final String HOOK_KEY = "mdc";

    @PostConstruct
    public void setup() {
        Hooks.onEachOperator(HOOK_KEY, Operators.lift((scannable, subscriber) -> new MdcContextLifter<>(subscriber)));
    }

    @PreDestroy
    public void cleanup() {
        Hooks.resetOnEachOperator(HOOK_KEY);
    }
}
