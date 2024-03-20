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

    public User addUser(User user) {
        return userRepository.save(user);
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
