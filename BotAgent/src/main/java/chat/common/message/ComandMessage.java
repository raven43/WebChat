package chat.common.message;

import java.util.Map;

public class ComandMessage {

    private ComandType type;
    private Map<String, String> payload;


    public ComandType getType() {
        return type;
    }

    public void setType(ComandType type) {
        this.type = type;
    }

    public Map<String, String> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, String> payload) {
        this.payload = payload;
    }

    public enum ComandType {
        REGISTER, LEAVE
    }
}

