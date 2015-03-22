package de.naeveke.board.board;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.io.Serializable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Type;

@Entity
public class Post implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @Embedded
    @NotNull
    private Position position;
    
    @NotNull
    @Type(type="text")
    private String content;
    
    @ManyToOne(optional = false)
    @JoinColumn(name="board_uuid")
    @JsonBackReference
    private Board board;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

}
