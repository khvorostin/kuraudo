package tech.kuraudo.common.message;

/**
 * Сообщение, через которые передаются данные для заливки на сервер. От сообщения, которое приходит с сервера,
 * отличается дополнительным атрибутом - путём к файлу.
 */
public class UploadContentMessage extends Message {

    private String path;
    private byte[] content;
    private long startPosition;
    private boolean last;

    public UploadContentMessage(String path, byte[] content, long startPosition, boolean last) {
        this.path = path;
        this.content = content;
        this.startPosition = startPosition;
        this.last = last;
    }

    // Пустой конструктор необходим для десериализации объектов jackson'ом.
    public UploadContentMessage() {
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
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
