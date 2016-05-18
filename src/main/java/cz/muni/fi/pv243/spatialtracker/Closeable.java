
package cz.muni.fi.pv243.spatialtracker;

import java.util.function.Consumer;
import javax.ws.rs.core.Response;
import static lombok.AccessLevel.PRIVATE;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = PRIVATE)
public class Closeable<T> implements AutoCloseable{

    private final T closeable;
    private final Consumer<T> close;

    public static Closeable<Response> closeable(final Response resp){
        return new Closeable<>(resp, (Response r) -> r.close());
    }

    @Override
    public void close() {
        this.close.accept(closeable);
    }

    public T get(){
        return this.closeable;
    }
}
