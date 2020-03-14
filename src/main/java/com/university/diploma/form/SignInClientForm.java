package com.university.diploma.form;

public class SignInClientForm {

    private final String sessionKey;
    private final Long userId;

    public SignInClientForm(String sessionKey, Long userId) {
        this.sessionKey = sessionKey;
        this.userId = userId;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public Long getUserId() {
        return userId;
    }
}
