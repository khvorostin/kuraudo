package tech.kuraudo.common.message;

public class FileContentMessage extends Message{

    private byte[] content;
    private long startPosition;
    private boolean last;

    public FileContentMessage(byte[] content, long startPosition, boolean last) {
        this.content = content;
        this.startPosition = startPosition;
        this.last = last;
    }

    // Пустой конструктор необходим для десериализации объектов jackson'ом.
    private FileContentMessage() {}

    public void setContent(byte[] content) {
        this.content = content;
    }

    public byte[] getContent() {
        return content;
    }

    public long getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(long startPosition) {
        this.startPosition = startPosition;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }
}
