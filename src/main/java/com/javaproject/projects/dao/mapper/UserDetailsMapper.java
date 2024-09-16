package com.javaproject.projects.dao.mapper;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserDetailsMapper implements RowMapper<UserDetails> {

  @Override
  public UserDetails map(ResultSet rs, StatementContext ctx) throws SQLException {
    return new User(rs.getString("email"), rs.getString("password"), List.of(new SimpleGrantedAuthority("ID_" + Long.toUnsignedString(rs.getLong("id")))));
  }
}
