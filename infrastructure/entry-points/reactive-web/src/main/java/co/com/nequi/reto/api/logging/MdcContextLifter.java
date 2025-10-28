package co.com.nequi.reto.api.logging;

import org.reactivestreams.Subscription;
import org.slf4j.MDC;
import reactor.core.CoreSubscriber;
import reactor.util.context.ContextView;

/**
 * CoreSubscriber decorator that lifts values from Reactor Context into SLF4J MDC
 * so that logging patterns (e.g., %X{cid}) are populated across thread hops.
 *
 * Currently propagates only the "cid" (Correlation ID) key.
 */
public class MdcContextLifter<T> implements CoreSubscriber<T> {

    private final CoreSubscriber<T> actual;

    public MdcContextLifter(CoreSubscriber<T> actual) {
        this.actual = actual;
    }

    @Override
    public void onSubscribe(Subscription s) {
        actual.onSubscribe(s);
    }

    @Override
    public void onNext(T t) {
        injectMdc();
        try {
            actual.onNext(t);
        } finally {
            MDC.clear();
        }
    }

    @Override
    public void onError(Throwable t) {
        injectMdc();
        try {
            actual.onError(t);
        } finally {
            MDC.clear();
        }
    }

    @Override
    public void onComplete() {
        injectMdc();
        try {
            actual.onComplete();
        } finally {
            MDC.clear();
        }
    }

    @Override
    public reactor.util.context.Context currentContext() {
        return actual.currentContext();
    }

    private void injectMdc() {
        ContextView ctx = actual.currentContext();
        ctx.<String>getOrEmpty("cid").ifPresent(v -> MDC.put("cid", v));
    }
}
