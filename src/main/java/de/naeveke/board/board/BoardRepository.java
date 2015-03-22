package de.naeveke.board.board;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Nitek
 */
@Repository
public interface BoardRepository extends JpaRepository<Board, UUID> {
}
