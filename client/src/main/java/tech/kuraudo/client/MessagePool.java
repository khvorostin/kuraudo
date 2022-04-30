package tech.kuraudo.client;

import tech.kuraudo.common.message.Message;

/**
 * Пул сообщений, вспомогательный класс для передачи данных между объектами, отвечающими за взаимодействие
 * с пользователем (см. {@link Handler}) и сетевое взаимодействие (см. {@link Messager}).
 */
public class MessagePool {
    private Message message;
    private boolean valueSet = false;

    synchronized void put(Message message) {
        while (valueSet) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        this.message = message;
        valueSet = true;
        notify();
    }

    synchronized Message get() {
        while (!valueSet) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        valueSet = false;
        notify();
        return message;
    }
}
