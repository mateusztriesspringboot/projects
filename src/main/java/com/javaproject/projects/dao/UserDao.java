package com.javaproject.projects.dao;

import com.javaproject.projects.dao.mapper.UserDetailsMapper;
import com.javaproject.projects.dao.mapper.UserMapper;
import com.javaproject.projects.model.User;
import org.jdbi.v3.spring5.JdbiRepository;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

@JdbiRepository
public interface UserDao {

  @SqlUpdate("INSERT INTO tb_user (email, password, name) VALUES (?, ?, ?)")
  void createUser(String email, String password, String name);

  @SqlQuery("SELECT LAST_INSERT_ID()")
  long getCreatedUserId();

  @SqlQuery("SELECT id, email, name FROM tb_user WHERE id=?")
  @RegisterRowMapper(UserMapper.class)
  Optional<User> getUser(long id);

  @SqlQuery("SELECT id, email, password FROM tb_user WHERE email=?")
  @RegisterRowMapper(UserDetailsMapper.class)
  Optional<UserDetails> getUser(String email);

  @SqlUpdate("DELETE FROM tb_user WHERE id=?")
  boolean deleteUser(long id);
}
