package com.university.diploma.service;

import java.util.List;

public interface DataService<T> {
    T create(T item);

    T findById(Long id);

    T update(T item);

    void remove(Long id);

    void remove(T item);

    List<T> findAll();
}
