package chat.client;

public enum Role {
    AGENT("Agent"),CLIENT("Client");
    public final String str;

    Role(String str) {
        this.str = str;
    }
}
