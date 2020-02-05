package com.university.diploma.container;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PojoContainer<T> {

    private Map<Long, T> items;

    public PojoContainer() {
        items = new HashMap<>();
    }

    public void addValue(Long key, T value) {
        items.putIfAbsent(key, value);
    }

    public T findValue(Long key) {
        return items.get(key);
    }

    public void remove(Long key) {
        T entity = items.get(key);
        if (entity != null) {
            items.remove(key);
        }
    }

    @SuppressWarnings("unchecked")
    public List<T> findAll() {
        List<T> list = new ArrayList<>(items.values());
        return list.size() != 0
                ? Collections.unmodifiableList(list)
                : Collections.EMPTY_LIST;
    }
}
