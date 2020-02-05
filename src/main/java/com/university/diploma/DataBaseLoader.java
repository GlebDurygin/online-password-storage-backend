package com.university.diploma;

import com.university.diploma.entity.Record;
import com.university.diploma.entity.User;
import com.university.diploma.repository.RecordHSQLRepository;
import com.university.diploma.repository.UserHSQLRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataBaseLoader implements ApplicationRunner {

    protected UserHSQLRepository userRepository;
    protected RecordHSQLRepository recordRepository;

    @Autowired
    public DataBaseLoader(UserHSQLRepository userRepository, RecordHSQLRepository recordRepository) {
        this.userRepository = userRepository;
        this.recordRepository = recordRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        User user1 = new User();
        user1.setUsername("Frodo");
        user1.setPassword("passwordBeggins");
        user1.setKeyword("Lord of rings");
        user1 = userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("admin");
        user2.setPassword("admin");
        user2.setKeyword("Lord");
        userRepository.save(user2);

        Record record = new Record();
        record.setUser(user1);
        record.setHeader("GMAIL");
        record.setData("data1111");
        record.setDescription("Mail service");
        recordRepository.save(record);

        userRepository.findAll().forEach(System.out::println);
        recordRepository.findAll().forEach(System.out::println);
    }
}
