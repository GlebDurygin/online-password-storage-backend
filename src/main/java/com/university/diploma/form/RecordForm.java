package com.university.diploma.form;

public class RecordForm {

    private final String header;
    private final String description;
    private final String data;

    public RecordForm(String header, String description, String data) {
        this.header = header;
        this.description = description;
        this.data = data;
    }

    public String getHeader() {
        return header;
    }

    public String getDescription() {
        return description;
    }

    public String getData() {
        return data;
    }
}
