package mo.communication.chat;

public class ChatEntry {
        public String name;
        public String content;

        // For type 0=sent, 1=received.
        public int type;

        public ChatEntry(String name, String content, int type) {
            this.name = name;
            this.content = content;
            this.type = type;
        }
    }