package com.university.diploma.dto;

public class UserSignInDBDto {
    protected final String salt;
    protected final String verifier;

    public UserSignInDBDto(String salt, String verifier) {
        this.salt = salt;
        this.verifier = verifier;
    }

    public String getSalt() {
        return salt;
    }

    public String getVerifier() {
        return verifier;
    }
}
