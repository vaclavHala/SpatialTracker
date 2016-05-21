package cz.muni.fi.pv243.spatialtracker.users.redmine;

import cz.muni.fi.pv243.spatialtracker.MulticauseError;
import cz.muni.fi.pv243.spatialtracker.users.UserService;
import java.io.IOException;
import java.security.Principal;
import java.security.acl.Group;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import lombok.extern.slf4j.Slf4j;
import org.jboss.security.SimpleGroup;
import org.jboss.security.SimplePrincipal;
import org.jboss.security.auth.spi.AbstractServerLoginModule;

@Slf4j
public class RedmineLoginModule extends AbstractServerLoginModule {
    
    protected String username;

    @Override
    public boolean login() throws LoginException {
        UserService userService;
        
        // CDI is not working since login modules are not managed beans
        // This is hack to get redmine users service
        try {
            InitialContext initialContext = new InitialContext();
            Object lookup = initialContext.lookup("java:comp/BeanManager");
            BeanManager beanManager = (BeanManager) lookup;
            userService = ((Bean<UserService>) beanManager.getBeans("RedmineUserService").iterator().next()).create(beanManager.createCreationalContext(null));
        } catch (NamingException ex) {
            log.error("Failed to get user service", ex);
            
            return false;
        }
        
        NameCallback nameCallback = new NameCallback("Login: ");
        PasswordCallback passwordCallback = new PasswordCallback("Password: ", false);
        
        try {
            callbackHandler.handle(new Callback[] { nameCallback, passwordCallback });
        } catch (IOException | UnsupportedCallbackException ex) {
            log.error("Error while getting credentials", ex);
        }
        
        username = nameCallback.getName();
        String password = passwordCallback.getPassword() == null ? null : new String(passwordCallback.getPassword());
        
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            log.debug("Empty credentials provided");
            
            return false;
        }
        
        try {
            return (loginOk = userService.detailsCurrentUser(username, password) != null);
        } catch (MulticauseError ex) {
            log.info("Login failed", ex);
        }
        
        return false;
    }

    @Override
    protected Group[] getRoleSets() throws LoginException {
        SimpleGroup group = new SimpleGroup("Roles");
        group.addMember(new SimplePrincipal("user"));

        return new Group[] { group };
    }

    @Override
    protected Principal getIdentity() {
        return new SimplePrincipal(username);
    }
}