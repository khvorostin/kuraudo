package tech.kuraudo.client;

import tech.kuraudo.common.message.AuthMessage;
import tech.kuraudo.common.message.Message;
import tech.kuraudo.common.message.TextMessage;

import java.util.Scanner;

/**
 * Заготовка класса, через который идёт взаимодействие с пользователем.
 * В текущей реализации запрашивает пару логин/пароль в консоли.
 */
public class Handler implements Runnable {

    private static final Scanner scanner = new Scanner(System.in);
    private final MessagePool messagePool;

    public Handler(MessagePool messagePool) {
        this.messagePool = messagePool;
    }

    @Override
    public void run() {
        // Мне кажется, что за работу с сетью и взаимодействие с пользователями должны отвечать разные классы.
        // Пока не придумал, как это сделать аккуратнее. Просто жду появления в пуле сообщений messagePool
        // появления сообщения [Successfully connection] и только после этого прошу ввести логин/пароль
        // Это сделано для того, чтобы не перемешивать в консоли вывод от двух разных потоков.
        Message message = messagePool.get();
        if (message instanceof TextMessage) {
            String messageText = ((TextMessage) message).getText();
            if (messageText.equals("Successfully connection")) {
                AuthMessage authMessage = new AuthMessage();
                System.out.print("Enter the login: ");
                authMessage.setLogin(scanner.next());
                System.out.print("Enter the password: ");
                authMessage.setPassword(scanner.next());
                messagePool.put(authMessage);
            }
        }
    }
}
