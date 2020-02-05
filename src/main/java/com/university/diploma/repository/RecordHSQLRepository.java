package com.university.diploma.repository;

import com.university.diploma.entity.Record;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordHSQLRepository extends PagingAndSortingRepository<Record, Long> {
}
