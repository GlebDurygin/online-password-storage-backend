package com.university.diploma;

import com.university.diploma.entity.Record;
import com.university.diploma.entity.User;
import com.university.diploma.service.RecordDataService;
import com.university.diploma.service.UserDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataBaseLoader implements ApplicationRunner {

    @Autowired
    protected UserDataService userDataService;
    @Autowired
    protected RecordDataService recordDataService;

    @Override
    public void run(ApplicationArguments args) {
        User anonymousUser = new User("anonymous", "anonymous", "anonymous");
        userDataService.create(anonymousUser);

        User user1 = new User();
        user1.setUsername("Frodo");
        user1.setPassword("passwordBeggins");
        user1.setKeyword("Lord of rings");
        user1 = userDataService.create(user1);

        User user2 = new User();
        user2.setUsername("admin");
        user2.setPassword("admin");
        user2.setKeyword("Lord");
        userDataService.create(user2);

        Record record = new Record();
        record.setUser(user1);
        record.setHeader("GMAIL");
        record.setData("data1111");
        record.setDescription("Mail service");
        recordDataService.create(record);
    }
}
