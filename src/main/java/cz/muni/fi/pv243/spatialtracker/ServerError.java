
package cz.muni.fi.pv243.spatialtracker;

public class ServerError extends RuntimeException{

    public ServerError(String message) {
        super(message);
    }

    public ServerError(Throwable cause) {
        super(cause);
    }
}
