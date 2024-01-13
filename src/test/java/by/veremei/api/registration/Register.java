package by.veremei.api.registration;

public class Register {
    private String email;
    private String password;
    public Register() {
    }

    public Register(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
