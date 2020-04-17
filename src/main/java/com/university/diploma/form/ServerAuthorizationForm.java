package com.university.diploma.form;

public class ServerAuthorizationForm {

    protected final String authorizationKey;
    protected final String salt;
    protected final String emphaticKeyB;

    public ServerAuthorizationForm(String authorizationKey, String salt, String emphaticKeyB) {
        this.authorizationKey = authorizationKey;
        this.salt = salt;
        this.emphaticKeyB = emphaticKeyB;
    }

    public String getSalt() {
        return salt;
    }

    public String getEmphaticKeyB() {
        return emphaticKeyB;
    }

    public String getAuthorizationKey() {
        return authorizationKey;
    }
}
