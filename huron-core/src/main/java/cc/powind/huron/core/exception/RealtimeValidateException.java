package cc.powind.huron.core.exception;

import cc.powind.huron.core.model.Realtime;
import cc.powind.huron.core.model.RealtimeException;

public class RealtimeValidateException extends RealtimeException {

    /**
     * 错误信息
     */
    private String[] errTexts;

    public RealtimeValidateException(Realtime realtime, String errText) {
        super(realtime);
        this.errTexts = new String[]{errText};
    }

    public RealtimeValidateException(Realtime realtime, String[] errTexts) {
        super(realtime);
        this.errTexts = errTexts;
    }

    public String[] getErrTexts() {
        return errTexts;
    }

    public void setErrTexts(String[] errTexts) {
        this.errTexts = errTexts;
    }
}
