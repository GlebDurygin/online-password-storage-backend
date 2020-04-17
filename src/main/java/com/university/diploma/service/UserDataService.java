package com.university.diploma.service;

import com.university.diploma.container.PojoContainer;
import com.university.diploma.dto.UserSignUpDto;
import com.university.diploma.entity.User;
import com.university.diploma.repository.UserHSQLRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
        User savedUser = userRepository.save(item);
        container.addValue(savedUser.getId(), savedUser);
        return savedUser;
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

    public boolean create(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        User savedUser = userRepository.save(user);
        container.addValue(savedUser.getId(), savedUser);
        return true;
    }

    public boolean create(UserSignUpDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getUsername());
        user.setVerifier(userDto.getVerifier());
        user.setSalt(userDto.getSalt());

        Page<User> userPage = userRepository.findUserByUsername(userDto.getUsername(), PageRequest.of(0, 1));
        if (userPage.isEmpty()) {
            User savedUser = userRepository.save(user);
            container.addValue(savedUser.getId(), savedUser);
            return true;
        } else {
            return false;
        }
    }

    public User findUserByUsername(String username) {
        if (username == null || username.isEmpty()) {
            return null;
        }

        Page<User> userPage = userRepository.findUserByUsername(username, PageRequest.of(0, 1));
        if (userPage.isEmpty()) {
            return null;
        } else {
            return userPage.get()
                    .findFirst()
                    .orElse(null);
        }
    }
}
