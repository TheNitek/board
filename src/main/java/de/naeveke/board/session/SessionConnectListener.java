package de.naeveke.board.session;

import javax.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;

@Component
public class SessionConnectListener implements ApplicationListener<SessionConnectEvent> {

    private final Log logger = LogFactory.getLog(getClass());
    
    @Inject
    private SessionService sessionService;

    @Override
    public void onApplicationEvent(SessionConnectEvent event) {
        StompHeaderAccessor header = StompHeaderAccessor.wrap(event.getMessage());
        
        String secret = header.getFirstNativeHeader("secret");
        
        sessionService.createSession(secret, header.getSessionId());
        
        logger.info("connecting: " + header.getSessionId());
    }

}
