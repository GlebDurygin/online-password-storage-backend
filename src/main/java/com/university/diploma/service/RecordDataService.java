package com.university.diploma.service;

import com.university.diploma.container.PojoContainer;
import com.university.diploma.entity.Record;
import com.university.diploma.entity.User;
import com.university.diploma.form.RecordForm;
import com.university.diploma.repository.RecordHSQLRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
        Record savedRecord = recordRepository.save(item);
        container.addValue(savedRecord.getId(), savedRecord);
        return savedRecord;
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
        if (recordRepository.existsById(id)) {
            recordRepository.deleteById(id);
        }
    }

    @Override
    public void remove(Record item) {
        if (recordRepository.existsById(item.getId())) {
            recordRepository.delete(item);
        }
    }

    @Override
    public List<Record> findAll() {
        return container.findAll();
    }

    public Record create(User user) {
        Record record = new Record();
        record.setUser(user);
        Record savedRecord = recordRepository.save(record);
        container.addValue(savedRecord.getId(), savedRecord);
        return savedRecord;
    }

    public Record createWithoutSaving(User user) {
        Record record = new Record();
        record.setUser(user);
        return record;
    }

    public Long create(RecordForm form, User user) {
        Record record = new Record();
        record.setHeader(form.getHeader());
        record.setData(form.getData());
        record.setDescription(form.getDescription());
        record.setUser(user);

        Record savedRecord = recordRepository.save(record);
        container.addValue(savedRecord.getId(), savedRecord);
        return savedRecord.getId();
    }

    public boolean update(Long recordId, RecordForm form, User user) {
        Record record = findById(recordId);
        if (record == null) {
            record = new Record();
        }
        record.setHeader(form.getHeader());
        record.setData(form.getData());
        record.setDescription(form.getDescription());
        record.setUser(user);

        update(record);
        return true;
    }

    public List<Record> findByUser(User user) {
        Page<Record> recordsPage = recordRepository.findRecordsByUser(user, PageRequest.of(0, 50));
        return recordsPage.hasContent()
                ? recordsPage.getContent()
                : null;
    }

    public Record findByIdAndUser(User user, Long recordId) {
        if (recordId == null) {
            return createWithoutSaving(user);
        }
        Page<Record> recordsPage = recordRepository.findRecordByIdAndUser(recordId, user, PageRequest.of(0, 1));
        return recordsPage.hasContent()
                ? recordsPage.getContent().get(0)
                : null;
    }
}
