package com.javaproject.projects.dao.mapper;

import com.javaproject.projects.model.Project;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProjectMapper implements RowMapper<Project> {
  @Override
  public Project map(ResultSet rs, StatementContext ctx) throws SQLException {
    return Project.builder().projectId(Long.toUnsignedString(rs.getLong("id")))
        .name(rs.getString("name")).build();
  }
}
