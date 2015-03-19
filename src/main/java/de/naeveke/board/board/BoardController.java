package de.naeveke.board.board;

import de.naeveke.board.common.ResourceNotFoundException;
import java.util.UUID;
import javax.inject.Inject;
import javax.validation.Valid;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class BoardController {

    private final Log logger = LogFactory.getLog(getClass());

    @Inject
    BoardService boardService;

    @Inject
    PostService postService;
    
    @Inject
    private SimpMessagingTemplate msgTemplate;
    
    private static final String WEBSOCKET_TOPIC = "/topic/";

    @RequestMapping(value = "/boards/{id}")
    @ResponseBody
    public Board getBoard(@PathVariable String id) {
        return boardService.get(UUID.fromString(id));
    }

    @RequestMapping(value = "/boards", method = RequestMethod.POST)
    public String createBoard(@Valid Board board) {

        boardService.save(board);

        return "redirect:/boards/" + board.getUuid();
    }

    @RequestMapping(value = "/boards/{boardId}/posts", method = RequestMethod.POST)
    public String createPost(@PathVariable String boardId, @RequestBody @Valid Post post) {
        Board board = boardService.get(UUID.fromString(boardId));

        if (null == board) {
            throw new ResourceNotFoundException();
        }

        board.addPost(post);

        boardService.save(board);
        
        // TODO move into service
        logger.debug("Sending Websocket create msg");
        msgTemplate.convertAndSend(WEBSOCKET_TOPIC + boardId, new StompEnvelope<Post>(post, StompEnvelope.Action.CREATE));

        return "redirect:/boards/" + boardId + "/posts/" + post.getId();
    }

    @RequestMapping(value = "/boards/{boardId}/posts/{postId}", method = RequestMethod.GET)
    @ResponseBody
    public Post getPost(@PathVariable String boardId, @PathVariable int postId) {
        Board board = boardService.get(UUID.fromString(boardId));

        if (null == board) {
            throw new ResourceNotFoundException();
        }

        for (Post post : board.getPosts()) {
            if (post.getId() == postId) {
                return post;
            }
        }

        throw new ResourceNotFoundException();
    }

    @RequestMapping(value = "/boards/{boardId}/posts/{postId}", method = RequestMethod.DELETE)
    @ResponseBody
    public void deletePost(@PathVariable String boardId, @PathVariable int postId) {
        Board board = boardService.get(UUID.fromString(boardId));

        if (null == board) {
            throw new ResourceNotFoundException();
        }

        Post deletePost = null;

        for (Post post : board.getPosts()) {
            if (post.getId() == postId) {
                deletePost = post;
                break;
            }
        }

        if (null == deletePost) {
            throw new ResourceNotFoundException();
        }

        board.removePost(deletePost);
        boardService.save(board);
        
        
        // TODO move into service
        logger.debug("Sending Websocket delete msg");
        msgTemplate.convertAndSend(WEBSOCKET_TOPIC + boardId, new StompEnvelope<Post>(deletePost, StompEnvelope.Action.DELETE));
    }

    @RequestMapping(value = "/boards/{boardId}/posts/{postId}", method = RequestMethod.POST)
    @ResponseBody
    public Post updatePost(@PathVariable String boardId, @PathVariable long postId, @RequestBody @Valid Post post) {

        Board board = boardService.get(UUID.fromString(boardId));

        if (null == board) {
            throw new ResourceNotFoundException();
        }

        Post updatedPost = null;
        
        // TODO solve in a better way
        for (Post existingPost : board.getPosts()) {
            if (existingPost.getId() == postId) {
                existingPost.setContent(post.getContent());
                existingPost.setPosition(post.getPosition());
                updatedPost = existingPost;
                break;
            }
        }
        
        if(null == updatedPost){
            throw new ResourceNotFoundException();
        }

        boardService.save(board);
        
        // TODO move into service
        logger.debug("Sending Websocket update msg");
        msgTemplate.convertAndSend(WEBSOCKET_TOPIC + boardId, new StompEnvelope<Post>(updatedPost, StompEnvelope.Action.UPDATE));

        return updatedPost;
    }
    
}
