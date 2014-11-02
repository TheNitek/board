package de.naeveke.board.board;

import java.util.UUID;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class BoardService {

    @Inject
    private BoardRepository boardRepo;

    public Board get(UUID id) {
        return boardRepo.findById(id);
    }
    
    public void save(Board board){
        boardRepo.save(board);
    }

}
