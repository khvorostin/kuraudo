package tech.kuraudo.common.message;

public class ActionResultMessage extends Message{

    private String actionResult;

    public ActionResultMessage(String actionResult) {
        this.actionResult = actionResult;
    }

    public ActionResultMessage() {
    }

    public String getActionResult() {
        return actionResult;
    }

    public void setActionResult(String actionResult) {
        this.actionResult = actionResult;
    }
}
