package com.university.diploma.dto;

public class UserSignInClientDto {
    protected final String username;
    protected final String emphaticKey;

    public UserSignInClientDto(String username, String emphaticKey) {
        this.username = username;
        this.emphaticKey = emphaticKey;
    }

    public String getUsername() {
        return username;
    }

    public String getEmphaticKey() {
        return emphaticKey;
    }
}
