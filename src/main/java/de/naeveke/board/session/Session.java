package de.naeveke.board.session;

import de.naeveke.board.user.User;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Session implements Serializable {
    
    @Id
    private String id;
    
    @ManyToOne(optional = false)
    @JoinColumn(name="session_user")
    private User user;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
