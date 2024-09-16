package com.javaproject.projects.dao;

import com.javaproject.projects.dao.mapper.ProjectMapper;
import com.javaproject.projects.model.Project;
import org.jdbi.v3.spring5.JdbiRepository;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

@JdbiRepository
public interface ProjectDao {

  @SqlUpdate("INSERT INTO tb_project (user_id, name) VALUES (?, ?)")
  void createProject(long userId, String name);

  @SqlQuery("SELECT LAST_INSERT_ID()")
  long getCreatedProjectId();

  @SqlQuery("SELECT id, name FROM tb_project WHERE user_id=?")
  @RegisterRowMapper(ProjectMapper.class)
  List<Project> listProjects(long userId);
}
