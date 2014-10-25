package de.naeveke.board.board;

import java.util.UUID;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class BoardService {

    @Inject
    private BoardRepository domainRepo;

    public Board get(UUID id) {
        return domainRepo.findById(id);
    }
    
    public void save(Board board){
        domainRepo.save(board);
    }

}
