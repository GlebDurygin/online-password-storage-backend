package com.university.diploma.dto;

public class UserSignUpDto {
    protected final String username;
    protected final String salt;
    protected final String verifier;

    public UserSignUpDto(String username, String salt, String verifier) {
        this.username = username;
        this.salt = salt;
        this.verifier = verifier;
    }

    public String getUsername() {
        return username;
    }

    public String getSalt() {
        return salt;
    }

    public String getVerifier() {
        return verifier;
    }
}
