package tech.kuraudo.common.message;

/**
 * Запрос аутентификации на сервере.
 */
public class AuthMessage extends Message {
    private String login;
    private String password;

    public AuthMessage(String login, String password) {
        this.login = login;
        this.password = password;
    }

    // Пустой конструктор необходим для десериализации объектов jackson'ом.
    private AuthMessage() {}

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "AuthMessage{login='" + login + "', password='" + password + "'}";
    }
}
