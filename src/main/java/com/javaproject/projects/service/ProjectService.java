package com.javaproject.projects.service;

import com.javaproject.projects.dao.ProjectDao;
import com.javaproject.projects.model.CreateProjectDetails;
import com.javaproject.projects.model.Project;
import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service for Project create and read operations.
 */
@Slf4j
@Service
public class ProjectService {

  private final ProjectDao projectDao;

  public ProjectService(ProjectDao projectDao) {
    this.projectDao = projectDao;
  }

  /**
   * Lists project for given user id
   * @param userId - user id
   * @return List containing all projects linked to the user. Empty list if no projects found.
   */
  public List<Project> listProjects(long userId) {
    return projectDao.listProjects(userId);
  }

  /**
   * Creates a project for given user id
   * @param userId - user id
   * @param createProjectDetails - new project details
   * @return Non-empty Optional with created Project or Empty Optional when user doesn't exist.
   */
  public Optional<Project> createProject(long userId, CreateProjectDetails createProjectDetails) {
    try {
      projectDao.createProject(userId, createProjectDetails.getName());
      long newId = projectDao.getCreatedProjectId();
      return Optional.of(Project.builder().projectId(Long.toUnsignedString(newId)).name(createProjectDetails.getName()).build());
    } catch (UnableToExecuteStatementException exception) {
      log.error("Error creating project name: '{}' for user id {}", createProjectDetails.getName(), userId, exception);
      return Optional.empty();
    }
  }
}
