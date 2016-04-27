package cz.muni.fi.pv243.spatialtracker;

import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.ext.Provider;

@Provider
public class ClientProducer {

    @Produces
    public Client make() {
        return ClientBuilder.newClient();
    }

    public void destroy(@Disposes final Client client) {
        client.close();
    }
}
