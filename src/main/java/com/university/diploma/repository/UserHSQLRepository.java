package com.university.diploma.repository;

import com.university.diploma.entity.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserHSQLRepository extends PagingAndSortingRepository<User, Long> {
}
