package de.naeveke.board.security;

import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserResource {
    
    @RequestMapping(value = "/users/current")
    @ResponseBody
    public Principal getBoard(Principal principal) {
        return principal;
    }
}
