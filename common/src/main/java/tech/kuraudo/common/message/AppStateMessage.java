package tech.kuraudo.common.message;

import tech.kuraudo.common.AppState;

@InnerMessage
public class AppStateMessage extends Message {

    private AppState appState;

    public AppStateMessage(AppState appState) {
        this.appState = appState;
    }

    public AppState getAppState() {
        return appState;
    }
}
