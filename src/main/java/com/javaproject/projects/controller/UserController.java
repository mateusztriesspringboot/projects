package com.javaproject.projects.controller;

import com.javaproject.projects.api.UsersApi;
import com.javaproject.projects.model.*;
import com.javaproject.projects.service.ProjectService;
import com.javaproject.projects.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.function.Supplier;

@Slf4j
@RestController
public class UserController implements UsersApi {
  public static final Supplier<ResponseStatusException> USER_NOT_FOUND = () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Not authorized or user not found.");
  public static final Supplier<ResponseStatusException> EMAIL_ALREADY_EXISTS = () -> new ResponseStatusException(HttpStatus.CONFLICT, "Provided email address already exists.");
  private final HttpServletRequest request;
  private final UserService userService;
  private final ProjectService projectService;

  public UserController(HttpServletRequest request, UserService userService, ProjectService projectService) {
    this.request = request;
    this.userService = userService;
    this.projectService = projectService;
  }

  @Override
  public ResponseEntity<User> getUser(String userId) {
    log.info("getUser. User id: {}", userId);
    authorizeUserAccess(userId);
    User user = userService.getUser(Long.parseUnsignedLong(userId))
        .orElseThrow(USER_NOT_FOUND);
    return ResponseEntity.ok(user);
  }

  @Override
  public ResponseEntity<User> createUser(CreateUserDetails createUserDetails) {
    log.info("createUser. CreateUserDetails: {}", createUserDetails);
    User user = userService.createUser(createUserDetails)
        .orElseThrow(EMAIL_ALREADY_EXISTS);
    return ResponseEntity.ok(user);
  }

  @Override
  public ResponseEntity<Void> deleteUser(String userId) {
    log.info("deleteUser. User id: {}", userId);
    authorizeUserAccess(userId);
    if (!userService.deleteUser(Long.parseUnsignedLong(userId))) {
      throw USER_NOT_FOUND.get();
    }
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @Override
  public ResponseEntity<Project> createProject(String userId, CreateProjectDetails createProjectDetails) {
    log.info("createProject. User id: {}, CreateProjectDetails: {}", userId, createProjectDetails);
    authorizeUserAccess(userId);
    Project project = projectService.createProject(Long.parseUnsignedLong(userId), createProjectDetails)
        .orElseThrow(USER_NOT_FOUND);
    return ResponseEntity.ok(project);
  }

  @Override
  public ResponseEntity<ProjectCollection> listUserProjects(String userId) {
    log.info("listUserProjects. User id: {}", userId);
    authorizeUserAccess(userId);
    List<Project> projects = projectService.listProjects(Long.parseUnsignedLong(userId));
    return ResponseEntity.ok(ProjectCollection.builder().items(projects).build());
  }

  private void authorizeUserAccess(String userId) {
    UsernamePasswordAuthenticationToken user = (UsernamePasswordAuthenticationToken) request.getUserPrincipal();
    String idAuthority = "ID_" + userId;
    user.getAuthorities().stream().filter(grantedAuthority -> idAuthority.equals(grantedAuthority.getAuthority())).findAny().orElseThrow(USER_NOT_FOUND);
  }
}
