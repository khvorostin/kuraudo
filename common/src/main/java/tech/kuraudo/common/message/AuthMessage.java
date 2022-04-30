package tech.kuraudo.common.message;

public class AuthMessage extends Message {
    private String login;
    private String password;

    synchronized public String getLogin() {
        return login;
    }

    synchronized public void setLogin(String login) {
        this.login = login;
    }

    synchronized public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "AuthMessage{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
