package com.university.diploma.form;

public class ServerAuthenticationForm {

    protected final String authenticationKey;
    protected final String salt;
    protected final String emphaticKeyB;

    public ServerAuthenticationForm(String authenticationKey, String salt, String emphaticKeyB) {
        this.authenticationKey = authenticationKey;
        this.salt = salt;
        this.emphaticKeyB = emphaticKeyB;
    }

    public String getSalt() {
        return salt;
    }

    public String getEmphaticKeyB() {
        return emphaticKeyB;
    }

    public String getAuthenticationKey() {
        return authenticationKey;
    }
}
