package com.javaproject.projects.service;

import com.javaproject.projects.dao.UserDao;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectsSchemaUserDetailsServiceTest {

  private ProjectsSchemaUserDetailsService projectsSchemaUserDetailsService;
  private final UserDao userDaoMock = Mockito.mock(UserDao.class);

  @BeforeEach
  void setUp() {
    projectsSchemaUserDetailsService = new ProjectsSchemaUserDetailsService(userDaoMock);
  }

  @Test
  void loadUserByUsername() {
    when(userDaoMock.getUser("test@test.com")).thenReturn(Optional.of(User.builder().username("test@test.com").password("password").build()));

    UserDetails userDetails = projectsSchemaUserDetailsService.loadUserByUsername("test@test.com");

    Assertions.assertNotNull(userDetails);
    verify(userDaoMock, times(1)).getUser("test@test.com");
  }

  @Test
  void loadUserByUsernameNotFound() {
    when(userDaoMock.getUser("test@test.com")).thenReturn(Optional.empty());

    UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> projectsSchemaUserDetailsService.loadUserByUsername("test@test.com"));

    assertEquals("User with email 'test@test.com' not found for authentication.", exception.getMessage());
    verify(userDaoMock, times(1)).getUser("test@test.com");
  }
}