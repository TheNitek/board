package de.naeveke.board.session;

import de.naeveke.board.user.User;
import de.naeveke.board.user.UserRepository;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class SessionService {
    
    private final Log logger = LogFactory.getLog(getClass());

    @Inject
    private SessionRepository sessionRepo;
    
    @Inject
    private UserRepository userRepo;
    
    private static volatile int userCount = 0;
    
    public Session createSession(String secret, String sessionId){
        logger.info("Creating session for: " + sessionId);
        User user = userRepo.findBySecret(secret);
        
        if(null == user){
            user = new User();
            user.setName("Neo" + userCount++);
            user.setSecret(secret);
        }
        
        userRepo.save(user);
        
        Session session = new Session();
        session.setId(sessionId);
        session.setUser(user);
        sessionRepo.save(session);
        
        return session;
    }
    
    public void deleteSession(String sessionId){
        Session session = sessionRepo.findOne(sessionId);
        if(null != session){
            sessionRepo.delete(sessionId);
        }
    }

}
