package de.naeveke.board.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Formula;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "websocket", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Entity
public class User implements Serializable {

    @Id
    @GeneratedValue
    private long id;

    @NotNull
    private String name;
    
    @NotNull
    @Column(unique = true)
    @JsonIgnore
    private String secret;
    
    @Formula("(SELECT EXISTS (SELECT 1 FROM Session s WHERE s.session_user = id))")
    @JsonIgnore
    private boolean online;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public boolean isOnline() {
        return online;
    }

}
