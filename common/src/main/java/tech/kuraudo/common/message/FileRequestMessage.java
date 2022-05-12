package tech.kuraudo.common.message;

public class FileRequestMessage extends Message {

    private String path;

    public FileRequestMessage(String path) {
        this.path = path;
    }

    // Пустой конструктор необходим для десериализации объектов jackson'ом.
    private FileRequestMessage() {}

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
