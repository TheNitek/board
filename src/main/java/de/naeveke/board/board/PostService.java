package de.naeveke.board.board;

import javax.inject.Inject;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class PostService {

    @Inject
    private PostRepository postRepo;

    public Post get(int id) {
        return postRepo.findById(id);
    }
    
    public void save(Post post){
        postRepo.save(post);
    }

}
