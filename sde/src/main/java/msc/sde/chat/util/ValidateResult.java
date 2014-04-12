package msc.sde.chat.util;

/**
 * @author: prabath
 */
public class ValidateResult {

    protected boolean isValid;

    protected String returnMsg;

    public ValidateResult() {
    }

    public ValidateResult(boolean valid, String returnMsg) {
        isValid = valid;
        this.returnMsg = returnMsg;
    }

    public String getReturnMsg() {
        return returnMsg;
    }

    public void setReturnMsg(String returnMsg) {
        this.returnMsg = returnMsg;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }
}
