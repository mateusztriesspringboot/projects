package com.javaproject.projects.service;

import com.javaproject.projects.dao.UserDao;
import com.javaproject.projects.model.CreateUserDetails;
import com.javaproject.projects.model.User;
import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


/**
 * Service for User create, read and delete operations.
 */
@Slf4j
@Service
public class UserService {

  private final UserDao userDao;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserDao userDao, PasswordEncoder passwordEncoder) {
    this.userDao = userDao;
    this.passwordEncoder = passwordEncoder;
  }

  /**
   * Creates user.
   * @param createUserDetails - create user details
   * @return Non-empty Optional with created User or Empty Optional when user already exists.
   */
  public Optional<User> createUser(CreateUserDetails createUserDetails) {
    try {
      userDao.createUser(createUserDetails.getEmail(), encodePassword(createUserDetails.getPassword()), createUserDetails.getName());
      long newId = userDao.getCreatedUserId();
      return Optional.of(User.builder().userId(Long.toUnsignedString(newId)).email(createUserDetails.getEmail()).name(createUserDetails.getName()).build());
    } catch (UnableToExecuteStatementException exception) {
      log.error("Error creating user with email: '{}' name: '{}'.", createUserDetails.getEmail(), createUserDetails.getName(), exception);
      return Optional.empty();
    }
  }

  /**
   * Deletes user.
   * @param userId - user id
   * @return true if user deleted successfully, false when no deletion happened.
   */
  public boolean deleteUser(long userId) {
    return userDao.deleteUser(userId);
  }

  /**
   * Reads user.
   * @param userId - user id
   * @return Non-empty Optional with User or Empty Optional when user doesn't exist.
   */
  public Optional<User> getUser(long userId) {
    return userDao.getUser(userId);
  }

  /**
   * Encodes password using registered Password Encoder.
   * @param password - password in plain text
   * @return encoded password
   */
  private String encodePassword(String password) {
    return passwordEncoder.encode(password);
  }
}
