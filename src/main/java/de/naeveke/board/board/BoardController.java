package de.naeveke.board.board;

import java.util.UUID;
import javax.inject.Inject;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class BoardController {

    @Inject
    BoardService boardService;
    
    @Inject
    PostService postService;

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

    @RequestMapping(value = "/boards/{id}/posts", method = RequestMethod.POST)
    public String createPost(@PathVariable String id, @Valid Post post) {
        Board board = boardService.get(UUID.fromString(id));

        board.getPosts().add(post);

        boardService.save(board);

        return "redirect:/boards/" + id + "/posts/" + post.getId();
    }

    @RequestMapping(value = "/boards/{id}/posts/{postId}", method = RequestMethod.POST)
    public String updatePost(@PathVariable String id, @PathVariable long postId, @Valid Post post) {
        Board board = boardService.get(UUID.fromString(id));

        // TODO solve in a better way
        for (Post existingPost : board.getPosts()) {
            if (existingPost.getId() == postId) {
                existingPost.setContent(post.getContent());
                existingPost.setPosition(post.getPosition());
            }
        }

        boardService.save(board);

        return "redirect:/boards/" + id + "/posts/" + post.getId();
    }
}
