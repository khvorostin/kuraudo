package tech.kuraudo.client;

import tech.kuraudo.common.AppState;
import tech.kuraudo.common.message.*;

import java.util.Scanner;

/**
 * Объект, реализующий взаимодействие с пользователем.
 */
public class Handler implements Runnable {

    /**
     * Название блока кода, которое используется в пуле сообщений для обозначения получателей сообщений.
     */
    public static final String MODULE_NAME = Handler.class.getName();

    /**
     * Пул сообщений
     */
    private final MessagePool messagePool;

    /**
     * Сканер, который используется в текущей реализации для получения из консоли данных от пользователя.
     */
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Состояние системы в текущий момент.
     */
    private AppState appState = AppState.CLIENT_UP;

    public Handler(MessagePool messagePool) {
        this.messagePool = messagePool;
    }

    @Override
    public void run() {

        // Крутим бесконечный цикл, в котором опрашиваем пул сообщений и обрабатываем сообщения от блока, отвечающего
        // за взаимодействие с сервером.
        while (true) {
            Message message = messagePool.get(MODULE_NAME);
            if (message instanceof AppStateMessage appStateMessage) {
                AppState newAppState = appStateMessage.getAppState();
                switch (newAppState) {
                    case CONNECTED -> {
                        if (this.appState == AppState.CLIENT_UP) {
                            this.appState = AppState.CONNECTED;
                            askCredentials();
                        }
                    }
                    case AUTHORIZED -> {
                        if (this.appState == AppState.CONNECTED) {
                            this.appState = AppState.AUTHORIZED;
                            askToMoveFile();
                        }
                    }
                    case SUCCESS -> {
                        if (this.appState == AppState.WAITING_FOR_DOWNLOADING) {
                            System.out.println("File downloaded successfully");
                            this.appState = AppState.AUTHORIZED;
                            askToMoveFile(); // продолжаем спрашивать клиента о том, что нужно скачать
                        }
                    }
                    case FAILURE -> {
                        if (this.appState == AppState.WAITING_FOR_DOWNLOADING) {
                            System.out.println("There was a problem downloading the file, please try again...");
                            this.appState = AppState.AUTHORIZED;
                            askToMoveFile(); // продолжаем спрашивать клиента о том, что нужно скачать
                        }
                    }
                }
            }
        }
    }

    /**
     * Запрашивает у пользователя логин и пароль для подключения к серверу, формирует сообщение для авторизации
     * или регистрации на сервере и бросает его в пул сообщений.
     */
    private void askCredentials() {
        System.out.println("Choose [1] to log in or [2] to register new user: ");
        int choose = scanner.nextInt();
        if (choose == 1 || choose == 2) {
            System.out.print("Enter the login: ");
            String login = scanner.next();
            System.out.print("Enter the password: ");
            String password = scanner.next();
            switch (choose) {
                case 1 -> messagePool.put(Messager.MODULE_NAME, new AuthMessage(login, password));
                case 2 -> {
                    if (!password.equals(scanner.next())) {
                        System.out.println("Passwords not equal!");
                    } else {
                        System.out.print("Enter your email: ");
                        String email = scanner.next();
                        messagePool.put(Messager.MODULE_NAME, new RegMessage(login, password, email));
                    }
                }
            }
        }
    }

    /**
     * Запрашивает у пользователя путь к файлу (который нужно скачать) на сервере и путь на локальной машине (куда
     * нужно сохранить данные), формирует запрос на копирование и бросает его в пул сообщений. Статус программы при этом
     * меняется на "Ожидание скачивания", что необходимо для вывода сообщений об успешности операции.
     */
    private void askToMoveFile() {
        System.out.print("Choose [1] for download file from server or [2] for upload to server: ");
        int choose = scanner.nextInt();
        if (choose == 1 || choose == 2) {
            System.out.print("Enter path to file on server: ");
            String pathOnServer = scanner.next();
            System.out.print("Enter path to new file on client: ");
            String pathOnClient = scanner.next();
            switch (choose) {
                case 1 -> {
                    messagePool.put(Messager.MODULE_NAME, new RequestToDownload(pathOnServer, pathOnClient));
                    this.appState = AppState.WAITING_FOR_DOWNLOADING;
                }
                case 2 -> {
                    messagePool.put(Messager.MODULE_NAME, new RequestToUpload(pathOnServer, pathOnClient));
                    this.appState = AppState.WAITING_FOR_UPLOADING;
                }
            }
        }
    }
}
