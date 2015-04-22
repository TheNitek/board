package de.naeveke.board.user;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Nitek
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    public User findBySecret(String secret);
    
    public List<User> findByOnline(boolean online);
    
}
