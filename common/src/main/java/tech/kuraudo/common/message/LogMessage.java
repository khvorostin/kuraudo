package tech.kuraudo.common.message;

public class LogMessage extends Message {

    private String log;

    public LogMessage(String log) {
        this.log = log;
    }

    // Пустой конструктор необходим для десериализации объектов jackson'ом.
    private LogMessage() {}

    public void setLog(String log) {
        this.log = log;
    }

    public String getLog() {
        return log;
    }

    @Override
    public String toString() {
        return "LogMessage{log='" + log + "'}";
    }
}
