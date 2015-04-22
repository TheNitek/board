package de.naeveke.board.board;

import de.naeveke.board.common.InvalidInputException;
import de.naeveke.board.config.WebSocketConfig;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
public class BoardResource {

    private final Log logger = LogFactory.getLog(getClass());

    @Inject
    BoardService boardService;

    @Inject
    private SimpMessagingTemplate msgTemplate;

    @RequestMapping(value = "/boards/{id}")
    @ResponseBody
    public Board getBoard(@PathVariable String id) {
        return boardService.getBoard(id);
    }

    @RequestMapping(value = "/boards", method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public Board createBoard(@Valid Board board, HttpServletResponse response, UriComponentsBuilder uriBuilder) {

        if (null != board.getUuid()) {
            throw new InvalidInputException();
        }

        boardService.createBoard(board);

        UriComponents uriComponents = uriBuilder.path("/boards/{id}").buildAndExpand(board.getUuid());
        response.setHeader("Location", uriComponents.toUriString());

        return board;
    }

    @RequestMapping(value = "/boards/{boardId}/posts", method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public Post createPost(
            @PathVariable String boardId,
            @RequestHeader("X-Client-Identifier") String clientIdentifier,
            @RequestBody @Valid Post post,
            HttpServletResponse response,
            UriComponentsBuilder uriBuilder) {

        if (null != post.getId()) {
            throw new InvalidInputException();
        }

        boardService.addPost(boardId, post);

        UriComponents uriComponents = uriBuilder.path("/boards/{boardId}/posts/{postId}").buildAndExpand(boardId, post.getId());
        response.setHeader("Location", uriComponents.toUriString());

        sendNotification(boardId, post, StompEnvelope.Action.CREATE, clientIdentifier);

        return post;
    }

    @RequestMapping(value = "/boards/{boardId}/posts/{postId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(
            @PathVariable String boardId,
            @PathVariable long postId,
            @RequestHeader("X-Client-Identifier") String clientIdentifier) {

        Post post = boardService.removePost(boardId, postId);

        sendNotification(boardId, post, StompEnvelope.Action.DELETE, clientIdentifier);
    }

    @RequestMapping(value = "/boards/{boardId}/posts/{postId}", method = RequestMethod.POST)
    @ResponseBody
    public Post updatePost(
            @PathVariable String boardId,
            @PathVariable long postId,
            @RequestHeader("X-Client-Identifier") String clientIdentifier,
            @RequestBody @Valid Post post) {

        if (postId != post.getId()) {
            throw new InvalidInputException();
        }

        boardService.updatePost(boardId, post);

        sendNotification(boardId, post, StompEnvelope.Action.UPDATE, clientIdentifier);

        return post;
    }

    protected void sendNotification(String boardId, Post post, StompEnvelope.Action action, String clientIdentifier) {
        logger.debug("Sending Websocket " + action + " msg for " + post.getId());
        Map<String, Object> headers = new HashMap<>();
        if (null != clientIdentifier) {
            headers.put("sender", clientIdentifier);
        }
        msgTemplate.convertAndSend(WebSocketConfig.WEBSOCKET_TOPIC + "/boards." + boardId, new StompEnvelope<>(post, action), headers);
    }

}
