package de.naeveke.board.board;

import java.io.Serializable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
public class Post implements Serializable {

    @Id
    @GeneratedValue
    private long id;

    @Embedded
    private Position position;
    
    @NotEmpty
    @Type(type="text")
    private String content;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
