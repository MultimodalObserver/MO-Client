package mo.communication.notes;

public class Note {
        public String autor;
        public String content;

        // For type 0=sent, 1=received.
        public int type;

        public Note(String name, String content, int type) {
            this.autor = name;
            this.content = content;
            this.type = type;
        }
    }