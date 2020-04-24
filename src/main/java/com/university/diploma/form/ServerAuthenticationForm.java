package com.university.diploma.form;

public class ServerAuthenticationForm {

    protected final String salt;
    protected final String emphaticKeyB;

    public ServerAuthenticationForm(String salt, String emphaticKeyB) {
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
