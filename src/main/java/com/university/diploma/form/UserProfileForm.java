package com.university.diploma.form;

import com.university.diploma.entity.Record;

import java.util.List;

public class UserProfileForm extends SignUpForm {
    private final List<Record> records;

    public UserProfileForm(String username, String password, String keyword, List<Record> records) {
        super(username, password, keyword);
        this.records = records;
    }

    public List<Record> getRecords() {
        return records;
    }
}
