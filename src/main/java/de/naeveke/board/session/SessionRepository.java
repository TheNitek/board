package de.naeveke.board.session;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Nitek
 */
@Repository
public interface SessionRepository extends JpaRepository<Session, String> {
}
