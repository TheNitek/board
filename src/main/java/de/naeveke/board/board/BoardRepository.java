package de.naeveke.board.board;

import de.naeveke.board.database.BaseRepository;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Nitek
 */
@Repository
public class BoardRepository extends BaseRepository<Board, UUID> {

    public BoardRepository() {
        super(Board.class);
    }
}
