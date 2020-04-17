package com.university.diploma.form;

public class SignUpForm {

    private final byte[] username;
    private final byte[] keyword;
    private final byte[] verifier;
    private final byte[] salt;

    public SignUpForm(byte[] username, byte[] keyword, byte[] verifier, byte[] salt) {
        this.username = username;
        this.keyword = keyword;
        this.verifier = verifier;
        this.salt = salt;
    }

    public byte[] getUsername() {
        return username;
    }

    public byte[] getKeyword() {
        return keyword;
    }

    public byte[] getVerifier() {
        return verifier;
    }

    public byte[] getSalt() {
        return salt;
    }
}
