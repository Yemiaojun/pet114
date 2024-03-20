package com.example.wechat.service;

import com.example.wechat.model.User;
import com.example.wechat.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Optional<User> addUser(User user) {
        // 检查是否已经存在相同用户名的用户
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            // 用户名已存在，返回空Optional作为错误指示
            return Optional.empty();
        }
        // 用户名不存在，添加新用户
        User savedUser = userRepository.save(user);
        return Optional.of(savedUser);
    }

    public User setAuth(ObjectId id, String auth) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setAuth(auth);
            return userRepository.save(user);
        }
        return null;
    }

    public User changePasswordById(ObjectId id, String newPassword) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPassword(newPassword);
            return userRepository.save(user);
        }
        return null;
    }

    public User changeNameById(ObjectId id, String newName) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setUsername(newName);
            return userRepository.save(user);
        }
        return null;
    }

    public Optional<User> tryLogin(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password);
    }

    public Optional<User> findUserById(ObjectId id) {
        return userRepository.findById(id);
    }

    public User changeAvatar(ObjectId id, String newAvatarUrl) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setAvatarUrl(newAvatarUrl);
            return userRepository.save(user);
        }
        return null;
    }

    public String checkAuth(ObjectId id) {
        Optional<User> userOptional = userRepository.findById(id);
        return userOptional.map(User::getAuth).orElse(null);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
}
