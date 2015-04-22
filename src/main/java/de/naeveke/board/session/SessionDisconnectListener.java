package de.naeveke.board.session;

import javax.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class SessionDisconnectListener implements ApplicationListener<SessionDisconnectEvent> {

    private final Log logger = LogFactory.getLog(getClass());
    
    @Inject
    private SessionService sessionService;
    
    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor header = StompHeaderAccessor.wrap(event.getMessage());
        
        sessionService.deleteSession(header.getSessionId());
        
        logger.info("disconnected: " + header.getSessionId());
    }

}
