package tech.kuraudo.common.message;

/**
 * Запрос на загрузку файла с клиента на сервер.
 */
public class RequestToUpload extends RequestToCopy {

    public RequestToUpload(String pathOnServer, String pathOnClient) {
        super(pathOnServer, pathOnClient);
    }
}
