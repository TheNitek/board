package de.naeveke.board.user;

import java.util.List;
import javax.inject.Inject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserResource {
    
    @Inject
    UserRepository userRepo;
    
    @RequestMapping(value = "/boards/{id}/users")
    @ResponseBody
    public List<User> getBoard(@PathVariable String id) {
        return userRepo.findByOnline(true);
    }

}
