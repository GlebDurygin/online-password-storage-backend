package com.university.diploma.repository;

import com.university.diploma.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserHSQLRepository extends PagingAndSortingRepository<User, Long> {

/*    @Query(value = "SELECT u FROM diploma_User WHERE u.username = :username AND u.password = :password", nativeQuery = true)
    Page<User> findUserByUsernameAndPassword(
            @Param("username") String username,
            @Param("password") String password);*/

    Page<User> findUserByUsernameAndPassword(String username, String password,  Pageable pageable);
}
