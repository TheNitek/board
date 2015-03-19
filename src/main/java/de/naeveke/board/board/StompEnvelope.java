package de.naeveke.board.board;

public class StompEnvelope<T> {
    
    private final T payload;
    private final Action action;
    private final String type;
    
    public StompEnvelope(T payload, Action action){
        this.payload = payload;
        this.action = action;
        type = payload.getClass().getSimpleName();
    }

    public T getPayload() {
        return payload;
    }

    public Action getAction() {
        return action;
    }

    public String getType() {
        return type;
    }
    
    public enum Action {
        CREATE, UPDATE, DELETE;
    }

}
