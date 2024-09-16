package com.javaproject.projects.service;

import com.javaproject.projects.dao.UserDao;
import com.javaproject.projects.model.CreateUserDetails;
import com.javaproject.projects.model.User;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserServiceTest {
  public static final String ENCODED_STRING = "encodedString";
  public static final long ID = 1L;
  public static final String EMAIL = "test@test.com";
  public static final String NAME = "name";
  public static final String PASSWORD = "password";

  private UserService userService;
  private final UserDao userDaoMock = mock(UserDao.class);
  private final PasswordEncoder passwordEncoderMock = mock(PasswordEncoder.class);

  @BeforeEach
  void setUp() {
    when(passwordEncoderMock.encode(anyString())).thenReturn(ENCODED_STRING);
    userService = new UserService(userDaoMock, passwordEncoderMock);
  }

  @Test
  void createUser() {
    when(userDaoMock.getCreatedUserId()).thenReturn(ID);
    CreateUserDetails createUserDetails = CreateUserDetails.builder()
        .email(EMAIL)
        .name(NAME)
        .password(PASSWORD)
        .build();

    Optional<User> user = userService.createUser(createUserDetails);

    assertTrue(user.isPresent());
    assertEquals(user.get().getUserId(), Long.toUnsignedString(ID));
    assertEquals(user.get().getEmail(), createUserDetails.getEmail());
    assertEquals(user.get().getName(), createUserDetails.getName());

    verify(userDaoMock, times(1)).createUser(anyString(), anyString(), anyString());
    verify(userDaoMock, times(1)).getCreatedUserId();
  }

  @Test
  void createUserWithExistingEmail() {
    when(userDaoMock.getCreatedUserId()).thenReturn(ID);
    doThrow(UnableToExecuteStatementException.class).when(userDaoMock).createUser(anyString(), anyString(), anyString());
    CreateUserDetails createUserDetails = CreateUserDetails.builder()
        .email(EMAIL)
        .name(NAME)
        .password(PASSWORD)
        .build();

    Optional<User> user = userService.createUser(createUserDetails);

    assertTrue(user.isEmpty());

    verify(userDaoMock, times(1)).createUser(anyString(), anyString(), anyString());
    verify(userDaoMock, times(0)).getCreatedUserId();
  }

  @Test
  void deleteUser() {
    when(userDaoMock.deleteUser(ID)).thenReturn(true);

    assertTrue(userService.deleteUser(ID));
    verify(userDaoMock, times(1)).deleteUser(ID);
  }

  @Test
  void getUser() {
    when(userDaoMock.getUser(ID)).thenReturn(Optional.of(User.builder().userId(Long.toUnsignedString(ID)).email(EMAIL).name(NAME).build()));

    Optional<User> user = userService.getUser(ID);
    assertTrue(user.isPresent());
    assertEquals(ID, Long.parseUnsignedLong(user.get().getUserId()));

    verify(userDaoMock, times(1)).getUser(ID);
  }
}