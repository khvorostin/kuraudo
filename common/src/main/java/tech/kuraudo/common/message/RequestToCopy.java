package tech.kuraudo.common.message;

/**
 * Запрос на загрузку файла с сервера на клиент или с клиента на сервер. Передаётся из модуля, отвечающего
 * за взаимодействие с пользователем, в модуль, отвечающий за общение с сервером.
 */
public abstract class RequestToCopy extends Message {

    /**
     * Путь к файлу на сервере.
     */
    private final String pathOnServer;

    /**
     * Путь к файлу на клиенте.
     */
    private final String pathOnClient;

    public RequestToCopy(String pathOnServer, String pathOnClient) {
        this.pathOnServer = pathOnServer;
        this.pathOnClient = pathOnClient;
    }

    public String getPathOnClient() {
        return pathOnClient;
    }

    public String getPathOnServer() {
        return pathOnServer;
    }
}
