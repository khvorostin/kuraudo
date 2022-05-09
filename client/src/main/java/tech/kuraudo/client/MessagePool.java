package tech.kuraudo.client;

import tech.kuraudo.common.message.Message;

import java.util.HashMap;
import java.util.Map;

/**
 * Пул сообщений, вспомогательный класс для передачи данных между объектами, отвечающими за взаимодействие
 * с пользователем (см. {@link Handler}) и сетевое взаимодействие (см. {@link Messager}).
 *
 * В один момент времени пул позволяет хранить только одно сообщение на заданного получателя. Следующее сообщение
 * можно добавить только после того как сообщение будет получено.
 */
public class MessagePool {

    private Map<String, Message> pool = new HashMap<>();

    synchronized void put(String subscriber, Message message) {
        while (pool.containsKey(subscriber)) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        pool.put(subscriber, message);
        notify();
    }

    synchronized Message get(String subscriber) {
        while (!pool.containsKey(subscriber)) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Message message = pool.remove(subscriber);
        notify();
        return message;
    }
}
