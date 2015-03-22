package de.naeveke.board.security;

import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

public class TransparentAnonymousAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {
    
    public TransparentAnonymousAuthenticationFilter() {
        //this.setCheckForPrincipalChanges(false);
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest hsr) {
        logger.info("getPreAuthenticatedPrincipal " + hsr);
        
        //String identifier = hsr.getHeader("X-Client-Identifier");
        
        
        return UUID.randomUUID().toString();
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest hsr) {
        logger.info("getPreAuthenticatedCredentials " + hsr);

        return "asdf";
    }


}
