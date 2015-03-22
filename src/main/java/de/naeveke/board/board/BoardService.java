package de.naeveke.board.board;

import de.naeveke.board.common.InvalidInputException;
import de.naeveke.board.common.ResourceNotFoundException;
import java.util.UUID;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class BoardService {

    @Inject
    private BoardRepository boardRepo;

    @Inject
    private PostRepository postRepo;

    public Board getBoard(String id) {
        return boardRepo.findOne(UUID.fromString(id));
    }

    public void createBoard(Board board) {
        boardRepo.save(board);
    }

    public void addPost(String boardId, Post post) {

        Board board = this.getBoard(boardId);

        if (null == board) {
            throw new ResourceNotFoundException();
        }

        board.addPost(post);

        postRepo.saveAndFlush(post);
    }

    public Post removePost(String boardId, long postId) {

        Board board = this.getBoard(boardId);

        if (null == board) {
            throw new ResourceNotFoundException();
        }

        Post post = postRepo.findOne(postId);

        if (null == post) {
            throw new ResourceNotFoundException();
        }

        if(!post.getBoard().equals(board)){
            throw new InvalidInputException();
        }
        
        // Be done or delete will silently fail
        board.removePost(post);

        postRepo.delete(post);
        
        return post;
    }

    public void updatePost(String boardId, Post post) {
        Post currentPost = postRepo.findOne(post.getId());

        if (null == currentPost) {
            throw new ResourceNotFoundException();
        }

        // Make sure people only delete on boards they know of
        if (!boardId.equals(currentPost.getBoard().getUuid().toString())) {
            throw new InvalidInputException();
        }
        
        // Might be a better idea to copy editable attributes from post to currentPost instead
        post.setBoard(currentPost.getBoard());

        postRepo.save(post);

    }

}
