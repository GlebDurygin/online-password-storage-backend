package com.university.diploma.session;

import java.math.BigInteger;

/**
 * Used only for authorization.
 */
public class AuthorizationDetails {

    protected final String authorizationKey;

    protected String randomB;
    protected String salt;
    protected String verifier;
    protected String emphaticKeyA;
    protected String emphaticKeyB;

    public AuthorizationDetails(String authorizationKey) {
        this.authorizationKey = authorizationKey;
    }

    public String getAuthorizationKey() {
        return authorizationKey;
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
