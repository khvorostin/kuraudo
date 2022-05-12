package tech.kuraudo.common;

public enum AppState {
    CLIENT_UP,
    CONNECTED,
    AUTHORIZED,
    WAITING_FOR_DOWNLOADING,
    WAITING_FOR_UPLOADING, // @todo
    SUCCESS,
    FAILURE;
}
