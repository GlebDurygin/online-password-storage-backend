package com.university.diploma.form;

import com.university.diploma.entity.Record;

import java.util.List;

public class UserProfileForm {
    private final String username;
    private final List<Record> records;

    public UserProfileForm(String username, List<Record> records) {
        this.username = username;
        this.records = records;
    }

    public List<Record> getRecords() {
        return records;
    }

    public String getUsername() {
        return username;
    }
}
