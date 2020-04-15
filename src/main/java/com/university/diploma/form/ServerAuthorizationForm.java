package com.university.diploma.form;

public class ServerAuthorizationForm {

    protected final String salt;
    protected final String emphaticKeyB;

    public ServerAuthorizationForm(String salt, String emphaticKeyB) {
        this.salt = salt;
        this.emphaticKeyB = emphaticKeyB;
    }

    public String getSalt() {
        return salt;
    }

    public String getEmphaticKeyB() {
        return emphaticKeyB;
    }
}