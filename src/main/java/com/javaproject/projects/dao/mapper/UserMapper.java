package com.javaproject.projects.dao.mapper;

import com.javaproject.projects.model.User;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements RowMapper<User> {
  @Override
  public User map(ResultSet rs, StatementContext ctx) throws SQLException {
    return User.builder().userId(Long.toUnsignedString(rs.getLong("id")))
        .email(rs.getString("email"))
        .name(rs.getString("name")).build();
  }
}
