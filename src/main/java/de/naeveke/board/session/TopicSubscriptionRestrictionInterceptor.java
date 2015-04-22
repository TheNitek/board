package de.naeveke.board.session;

import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;

public class TopicSubscriptionRestrictionInterceptor extends ChannelInterceptorAdapter {

    private final Log logger = LogFactory.getLog(getClass());
    
    // Allowed format is /topic/boards.[uuid]
    private static final String  allowedTopicRegex = "\\/topic\\/boards.[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor header = StompHeaderAccessor.wrap(message);
        if (StompCommand.SUBSCRIBE.equals(header.getCommand())) {
            if (!isSubscriptionAllowed(header.getDestination())) {
                logger.warn("No permission for topic " + header.getDestination());
                return null;
            }
        }
        return message;
    }

    private boolean isSubscriptionAllowed(String topicDestination) {
        logger.debug("Validating subscription to topic " + topicDestination);
        return topicDestination.matches(allowedTopicRegex);
    }
}
