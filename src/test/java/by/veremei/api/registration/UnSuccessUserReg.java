package by.veremei.api.registration;

public class UnSuccessUserReg {
    private String error;
    public UnSuccessUserReg() {
    }

    public UnSuccessUserReg(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
