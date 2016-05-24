package cz.muni.fi.pv243.spatialtracker;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class ClientProducer {

    private final ClientBuilder builder =
            ClientBuilder.newBuilder();

    @Produces
    public Client make() {
        Client client = this.builder.build();
        log.debug("Create new REST Client ({})", System.identityHashCode(client));
        return client;
    }

    public void destroy(final @Disposes Client client) {
        log.debug("Close REST Client ({})", System.identityHashCode(client));
        client.close();
    }
}
