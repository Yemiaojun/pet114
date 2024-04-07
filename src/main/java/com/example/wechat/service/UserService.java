package com.example.wechat.service;

import com.example.wechat.exception.DefaultException;
import com.example.wechat.model.Exam;
import com.example.wechat.model.ExamRecord;
import com.example.wechat.model.QuestionRecord;
import com.example.wechat.model.User;
import com.example.wechat.repository.ExamRecordRepository;
import com.example.wechat.repository.ExamRepository;
import com.example.wechat.repository.QuestionRecordRepository;
import com.example.wechat.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRecordRepository questionRecordRepository;

    @Autowired
    private ExamRecordRepository examRecordRepository;

    @Autowired
    private ExamRepository examRepository;
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Optional<User> addUser(User user) {
        if (user.getUsername().length() < 2) {
            throw new DefaultException("用户名过短，至少需要两个字符");
        }

        if (user.getPassword().length() < 3) {
            throw new DefaultException("密码过短，至少需要3位");
        }

        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            throw new DefaultException("用户名已存在");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword())); // 加密密码
        user.setAvatarUrl("file/headpic/default");
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
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent() && passwordEncoder.matches(password, userOptional.get().getPassword())) {
            return userOptional;
        }
        return Optional.empty();
    }


    public Optional<User> findUserById(ObjectId id) {
        return userRepository.findById(id);
    }

    public List<User> findUsersByUsernameLike(String username) {
        // 构建一个正则表达式，进行不区分大小写的模糊匹配
        String regex = ".*" + username + ".*";
        return userRepository.findByUsernameLike(regex);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public boolean updatePassword(String username, String currentPassword, String newPassword) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(currentPassword, user.getPassword())) { // 验证密码
                user.setPassword(passwordEncoder.encode(newPassword)); // 加密新密码
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }


    public String getSecurityQuestionByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        return userOptional.map(User::getSecurityQuestion).orElse(null);
    }

    public boolean updatePasswordIfSecurityAnswerMatches(String username, String securityAnswer, String newPassword) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // 这里简化了安全问题答案的比较逻辑，实际应用中可能需要更复杂的安全考虑
            if (user.getSecurityQuestionAnswer().equals(securityAnswer)) {
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    public boolean updatePasswordById(ObjectId userId, String newPassword) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user); // 更新用户信息
            return true;
        }
        return false;
    }

    public boolean updateAuth(ObjectId userId, String auth) {
        if (!"1".equals(auth) && !"2".equals(auth)) {
            throw new DefaultException("无效的权限等级");
        }

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setAuth(auth); // 更新权限
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public void saveAvatarUrl(ObjectId userId, String avatarUrl) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setAvatarUrl(avatarUrl); // 更新用户的头像URL
            userRepository.save(user); // 保存更改
        }
        else throw new DefaultException("错误的userID");

    }

    @Transactional
    public void deleteUserById(String userId) {
        ObjectId userObjId = new ObjectId(userId);

        // 删除用户的所有问题记录
        List<QuestionRecord> questionRecords = questionRecordRepository.findByUserId(userObjId);
        questionRecordRepository.deleteAll(questionRecords);

        // 删除用户的所有考试记录
        List<ExamRecord> examRecords = examRecordRepository.findByUserId(userObjId);
        examRecordRepository.deleteAll(examRecords);

        // 更新考试中的用户引用
        List<Exam> exams = examRepository.findAll();
        exams.forEach(exam -> {
            if(exam.getHolder() != null && exam.getHolder().getId().equals(userObjId)) {
                exam.setHolder(null); // 如果用户是考试的持有者
            }
            exam.getParticipantList().removeIf(user -> user.getId().equals(userObjId)); // 移除参与者列表中的用户
            exam.getWhiteList().removeIf(user -> user.getId().equals(userObjId)); // 移除白名单列表中的用户
            examRepository.save(exam);
        });

        // 最后删除用户本身
        userRepository.deleteById(userObjId);
    }





}
