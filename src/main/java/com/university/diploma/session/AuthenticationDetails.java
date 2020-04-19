package com.university.diploma.session;

/**
 * Used only for authentication.
 */
public class AuthenticationDetails {

    protected final String authenticationKey;

    protected String randomB;
    protected String salt;
    protected String verifier;
    protected String emphaticKeyA;
    protected String emphaticKeyB;

    public AuthenticationDetails(String authenticationKey) {
        this.authenticationKey = authenticationKey;
    }

    public String getAuthenticationKey() {
        return authenticationKey;
    }

    public String getRandomB() {
        return randomB;
    }

    public void setRandomB(String randomB) {
        this.randomB = randomB;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getVerifier() {
        return verifier;
    }

    public void setVerifier(String verifier) {
        this.verifier = verifier;
    }

    public String getEmphaticKeyA() {
        return emphaticKeyA;
    }

    public void setEmphaticKeyA(String emphaticKeyA) {
        this.emphaticKeyA = emphaticKeyA;
    }

    public String getEmphaticKeyB() {
        return emphaticKeyB;
    }

    public void setEmphaticKeyB(String emphaticKeyB) {
        this.emphaticKeyB = emphaticKeyB;
    }
}
