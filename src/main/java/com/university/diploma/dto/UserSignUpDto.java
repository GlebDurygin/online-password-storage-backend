package com.university.diploma.dto;

public class UserSignUpDto {
    protected final String username;
    protected final String salt;
    protected final String verifier;
    protected final String keyword;

    public UserSignUpDto(String username, String salt, String verifier, String keyword) {
        this.username = username;
        this.salt = salt;
        this.verifier = verifier;
        this.keyword = keyword;
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

    public String getKeyword() {
        return keyword;
    }
}
