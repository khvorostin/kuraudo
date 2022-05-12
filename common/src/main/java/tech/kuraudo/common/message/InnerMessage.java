package tech.kuraudo.common.message;

/**
 * Аннотация для маркирования сообщений, которые могут использоваться только внутри одного модуля. Сообщения
 * с этой аннотацией нельзя передавать от клиента серверу или от сервера клиенту.
 */
public @interface InnerMessage {
}
