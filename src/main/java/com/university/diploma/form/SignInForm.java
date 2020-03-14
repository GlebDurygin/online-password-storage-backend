package com.university.diploma.form;

public class SignInForm {
    private final String username;
    private final String password;

    public SignInForm(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
