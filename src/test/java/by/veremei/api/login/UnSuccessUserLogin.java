package by.veremei.api.login;

public class UnSuccessUserLogin {
    private String error;

    public UnSuccessUserLogin() {
    }

    public UnSuccessUserLogin(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
