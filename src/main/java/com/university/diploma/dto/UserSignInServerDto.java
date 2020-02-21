package com.university.diploma.dto;

public class UserSignInServerDto {
    protected final String salt;
    protected final String emphaticKey;

    public UserSignInServerDto(String salt, String emphaticKey) {
        this.salt = salt;
        this.emphaticKey = emphaticKey;
    }

    public String getSalt() {
        return salt;
    }

    public String getEmphaticKey() {
        return emphaticKey;
    }
}
