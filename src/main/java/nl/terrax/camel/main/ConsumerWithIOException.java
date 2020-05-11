package nl.terrax.camel.main;

import java.io.IOException;

@FunctionalInterface
public interface ConsumerWithIOException<T> {
    void accept(T var1) throws IOException;
}
