package com.javaproject.projects.service;

import com.javaproject.projects.dao.ProjectDao;
import com.javaproject.projects.model.CreateProjectDetails;
import com.javaproject.projects.model.CreateUserDetails;
import com.javaproject.projects.model.Project;
import com.javaproject.projects.model.User;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ProjectServiceTest {

  private ProjectService projectService;
  private final ProjectDao projectDaoMock = Mockito.mock(ProjectDao.class);

  @BeforeEach
  void setUp() {
    projectService = new ProjectService(projectDaoMock);
  }

  @Test
  void listProjects() {
    when(projectDaoMock.listProjects(1L)).thenReturn(List.of(Project.builder().projectId("1").name("project").build()));

    List<Project> projects = projectService.listProjects(1L);

    assertEquals(1, projects.size());
    assertEquals("1", projects.get(0).getProjectId());
  }

  @Test
  void createProject() {
    when(projectDaoMock.getCreatedProjectId()).thenReturn(1L);
    CreateProjectDetails createProjectDetails = CreateProjectDetails.builder()
        .name("project")
        .build();

    Optional<Project> project = projectService.createProject(1L, createProjectDetails);

    assertTrue(project.isPresent());
    assertEquals(project.get().getProjectId(), Long.toUnsignedString(1L));
    assertEquals(project.get().getName(), createProjectDetails.getName());

    verify(projectDaoMock, times(1)).createProject(1L, "project");
    verify(projectDaoMock, times(1)).getCreatedProjectId();
  }

  @Test
  void createProjectUserNotFound() {
    when(projectDaoMock.getCreatedProjectId()).thenReturn(1L);
    doThrow(UnableToExecuteStatementException.class).when(projectDaoMock).createProject(1L, "project");
    CreateProjectDetails createProjectDetails = CreateProjectDetails.builder()
        .name("project")
        .build();

    Optional<Project> project = projectService.createProject(1L, createProjectDetails);

    assertTrue(project.isEmpty());

    verify(projectDaoMock, times(1)).createProject(1L, "project");
    verify(projectDaoMock, times(0)).getCreatedProjectId();
  }
}