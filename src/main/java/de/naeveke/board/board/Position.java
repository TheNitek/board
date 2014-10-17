package de.naeveke.board.board;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Embeddable
public class Position implements Serializable {

    @NotNull
    private int x;

    @NotNull
    private int y;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

}
