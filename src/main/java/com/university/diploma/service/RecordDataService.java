package com.university.diploma.service;

import com.university.diploma.container.PojoContainer;
import com.university.diploma.entity.Record;
import com.university.diploma.repository.RecordHSQLRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;

@Scope(value = "singleton")
@Service
public class RecordDataService implements DataService<Record> {

    private PojoContainer<Record> container;
    private RecordHSQLRepository recordRepository;

    @Autowired
    public RecordDataService(RecordHSQLRepository recordRepository) {
        this.recordRepository = recordRepository;
        this.container = new PojoContainer<>();
        recordRepository.findAll().forEach(item -> container.addValue(item.getId(), item));
    }

    public RecordDataService() {
        this.container = new PojoContainer<>();
    }

    @Override
    public Record create(Record item) {
        container.addValue(item.getId(), item);
        return recordRepository.save(item);
    }

    @Override
    public Record findById(Long id) {
        return container.findValue(id);
    }

    @Override
    public Record update(Record item) {
        container.remove(item.getId());
        container.addValue(item.getId(), item);
        return recordRepository.save(item);
    }

    @Override
    public void remove(Long id) {
        container.remove(id);
    }

    @Override
    public void remove(Record item) {
        container.remove(item.getId());
    }

    @Override
    public List<Record> findAll() {
        return container.findAll();
    }
}
