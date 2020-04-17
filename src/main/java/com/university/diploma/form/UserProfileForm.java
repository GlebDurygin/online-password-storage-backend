package com.university.diploma.form;

import com.university.diploma.entity.Record;

import java.util.List;

public class UserProfileForm {
    private final String username;
    private final String password;
    private final String keyword;
    private final List<Record> records;

    public UserProfileForm(String username, String password, String keyword, List<Record> records) {
        this.username = username;
        this.password = password;
        this.keyword = keyword;
        this.records = records;
    }

    public List<Record> getRecords() {
        return records;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getKeyword() {
        return keyword;
    }
}
