package tech.kuraudo.common.message;

/**
 * Запрос на скачивание файла с сервера на клиент
 */
public class RequestToDownload extends RequestToCopy {

    public RequestToDownload(String pathOnServer, String pathOnClient) {
        super(pathOnServer, pathOnClient);
    }
}
