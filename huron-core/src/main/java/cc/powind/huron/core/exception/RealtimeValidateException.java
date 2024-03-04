package cc.powind.huron.core.exception;

import cc.powind.huron.core.model.Realtime;
import cc.powind.huron.core.model.RealtimeException;

public class RealtimeValidateException extends RealtimeException {


    private String[] errTexts;

    public RealtimeValidateException(String[] errTexts) {
        this.errTexts = errTexts;
    }

    public String[] getErrTexts() {
        return errTexts;
    }

    public void setErrTexts(String[] errTexts) {
        this.errTexts = errTexts;
    }
}
