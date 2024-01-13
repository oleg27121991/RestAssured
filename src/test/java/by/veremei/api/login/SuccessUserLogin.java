package by.veremei.api.login;

public class SuccessUserLogin {
    private String token;

    public SuccessUserLogin() {
    }

    public SuccessUserLogin(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
