package cz.muni.fi.pv243.spatialtracker;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RunAs;
import javax.ejb.Stateless;
import java.util.concurrent.Callable;

@RunAs("USER")
@PermitAll
@Stateless
public class RunAsUser {
    public <V> V call(Callable<V> callable) throws Exception {
        return callable.call();
    }
}
