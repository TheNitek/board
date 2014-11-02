package de.naeveke.board.board;

import de.naeveke.board.database.BaseRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Nitek
 */
@Repository
public class PostRepository extends BaseRepository<Post, Integer> {

    public PostRepository() {
        super(Post.class);
    }
}
