package com.university.diploma.form;

public class SignUpForm {

    private final byte[] username;
    private final byte[] verifier;
    private final byte[] salt;

    public SignUpForm(byte[] username, byte[] verifier, byte[] salt) {
        this.username = username;
        this.verifier = verifier;
        this.salt = salt;
    }

    public byte[] getUsername() {
        return username;
    }

    public byte[] getVerifier() {
        return verifier;
    }

    public byte[] getSalt() {
        return salt;
    }
}
