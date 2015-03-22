package de.naeveke.board.board;

import de.naeveke.board.common.InvalidInputException;
import de.naeveke.board.common.ResourceNotFoundException;
import de.naeveke.board.config.WebSocketConfig;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class BoardService {

    private final Log logger = LogFactory.getLog(getClass());

    @Inject
    private BoardRepository boardRepo;

    @Inject
    private PostRepository postRepo;

    @Inject
    private SimpMessagingTemplate msgTemplate;

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

        logger.debug("Sending Websocket create msg for " + post.getId());
        msgTemplate.convertAndSend(WebSocketConfig.WEBSOCKET_TOPIC + "/boards/" + boardId, new StompEnvelope<>(post, StompEnvelope.Action.CREATE));
    }

    public void removePost(String boardId, long postId) {

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
        
        // Foreign Key must be remove or delete will silently fail
        post.setBoard(null);

        postRepo.delete(post);

        logger.debug("Sending Websocket remove msg for " + postId);
        msgTemplate.convertAndSend(WebSocketConfig.WEBSOCKET_TOPIC + "/boards/" + boardId, new StompEnvelope<>(post, StompEnvelope.Action.DELETE));
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
