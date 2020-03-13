package com.university.diploma.form;

public class SignUpForm {

    private final String username;
    private final String password;
    private final String keyword;

    public SignUpForm(String username, String password, String keyword) {
        this.username = username;
        this.password = password;
        this.keyword = keyword;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getKeyword() {
        return keyword;
    }
}
