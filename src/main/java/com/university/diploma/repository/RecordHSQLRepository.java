package com.university.diploma.repository;

import com.university.diploma.entity.Record;
import com.university.diploma.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordHSQLRepository extends PagingAndSortingRepository<Record, Long> {

    Page<Record> findRecordsByUser(User user, Pageable pageable);

    Page<Record> findRecordByIdAndUser(Long id, User user, Pageable pageable);
}
