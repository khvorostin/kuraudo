package tech.kuraudo.client;

import com.formdev.flatlaf.FlatDarculaLaf;

import javax.swing.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Клиент для работы с сетевым хранилищем.
 */
public class ClientApp {

    private final static String HOST = "localhost";
    private final static int PORT = 9000;

    public static void main(String[] args) throws InterruptedException {

        MessagePool messagePool = new MessagePool();
        try {
            UIManager.setLookAndFeel(new FlatDarculaLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }

        new GUI(messagePool);

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(new Messager(HOST, PORT, messagePool));
        executorService.submit(new Handler(messagePool));
        executorService.shutdown();
        System.out.println("Client app started");
        executorService.awaitTermination(1, TimeUnit.DAYS);
    }
}
