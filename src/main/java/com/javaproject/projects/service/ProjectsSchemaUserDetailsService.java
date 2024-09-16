package com.javaproject.projects.service;

import com.javaproject.projects.dao.UserDao;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Custom UserDetailsService implementation that uses project service user dao layer to load users for authentication.
 */
public class ProjectsSchemaUserDetailsService implements UserDetailsService {

  private final UserDao userDao;

  public ProjectsSchemaUserDetailsService(UserDao userDao) {
    this.userDao = userDao;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userDao.getUser(username).orElseThrow(() -> new UsernameNotFoundException("User with email '" + username + "' not found for authentication."));
  }
}
