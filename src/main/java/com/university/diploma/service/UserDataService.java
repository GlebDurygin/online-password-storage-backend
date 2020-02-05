package com.university.diploma.service;

import com.university.diploma.container.PojoContainer;
import com.university.diploma.entity.User;
import com.university.diploma.repository.UserHSQLRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;

@Scope(value = "singleton")
@Service
public class UserDataService implements DataService<User> {

    private PojoContainer<User> container;
    private UserHSQLRepository userRepository;

    @Autowired
    public UserDataService(UserHSQLRepository userRepository) {
        this.userRepository = userRepository;
        this.container = new PojoContainer<>();
        userRepository.findAll().forEach(item -> container.addValue(item.getId(), item));
    }

    public UserDataService() {
        this.container = new PojoContainer<>();
    }

    @Override
    public User create(User item) {
        container.addValue(item.getId(), item);
        return userRepository.save(item);
    }

    @Override
    public User findById(Long id) {
        return container.findValue(id);
    }

    @Override
    public User update(User item) {
        container.remove(item.getId());
        container.addValue(item.getId(), item);
        return userRepository.save(item);
    }

    @Override
    public void remove(Long id) {
        container.remove(id);
    }

    @Override
    public void remove(User item) {
        container.remove(item.getId());
    }

    @Override
    public List<User> findAll() {
        return container.findAll();
    }
}
