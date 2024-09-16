package com.javaproject.projects.controller;

import com.javaproject.projects.dao.ProjectDao;
import com.javaproject.projects.dao.UserDao;
import com.javaproject.projects.model.*;
import com.javaproject.projects.model.Error;
import com.javaproject.projects.service.UserService;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import javax.print.attribute.standard.MediaSize;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserControllerTest {

  public static final String EMAIL = "user@test.com";
  public static final String PASSWORD = "password";
  public static final String NAME = "name";
  public static final String NEW_PROJECT = "new Project";
  @Autowired
  private TestRestTemplate restTemplate;
  @Autowired
  private UserDao userDao;
  @Autowired
  private ProjectDao projectDao;
  @Autowired
  private Jdbi jdbi;
  @Autowired
  private PasswordEncoder passwordEncoder;

  @BeforeEach
  void setUp() {
    userDao.createUser(EMAIL, passwordEncoder.encode(PASSWORD), NAME);
  }

  @AfterEach
  void cleanUp() {
    try (Handle handle = jdbi.open()) {
      handle.execute("DELETE FROM tb_user");
    }
  }

  @Test
  void getUser() {
    long id = userDao.getCreatedUserId();
    HttpHeaders headers = new HttpHeaders();
    headers.setBasicAuth(EMAIL, PASSWORD);
    HttpEntity<?> entity = new HttpEntity<>(headers);

    ResponseEntity<User> response = restTemplate.exchange("/users/" + id,
        HttpMethod.GET,
        entity,
        User.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(Long.toUnsignedString(id), response.getBody().getUserId());
    assertEquals(NAME, response.getBody().getName());
    assertEquals(EMAIL, response.getBody().getEmail());
  }

  @Test
  void getUserErrorOutWhenDifferentUserRequested() {
    HttpHeaders headers = new HttpHeaders();
    headers.setBasicAuth(EMAIL, PASSWORD);
    HttpEntity<?> entity = new HttpEntity<>(headers);

    ResponseEntity<Error> response = restTemplate.exchange("/users/123",
        HttpMethod.GET,
        entity,
        Error.class);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Not authorized or user not found.", response.getBody().getMessage());
    assertEquals("Not Found", response.getBody().getError());
  }

  @Test
  void getUserErrorOutWhenUnauthorized() {
    ResponseEntity<Error> response = restTemplate.getForEntity("/users/1", Error.class);

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertEquals("Unauthorized", response.getBody().getMessage());
    assertEquals("Unauthorized", response.getBody().getError());
  }

  @Test
  void createUser() {
    CreateUserDetails createUserDetails = CreateUserDetails.builder()
        .email("test@test.com")
        .name(NAME)
        .password(PASSWORD)
        .build();
    ResponseEntity<User> response = restTemplate.postForEntity("/users", createUserDetails, User.class);

    User user = userDao.getUser(Long.parseUnsignedLong(response.getBody().getUserId())).orElseThrow(() -> new RuntimeException("User not created"));

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(NAME, response.getBody().getName());
    assertEquals("test@test.com", response.getBody().getEmail());
    assertNotNull(user);

    //Test if auth works for new user
    HttpHeaders headers = new HttpHeaders();
    headers.setBasicAuth("test@test.com", PASSWORD);
    HttpEntity<?> entity = new HttpEntity<>(headers);

    ResponseEntity<User> getResponse = restTemplate.exchange("/users/" + response.getBody().getUserId(),
        HttpMethod.GET,
        entity,
        User.class);

    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
  }

  @Test
  void createUserErrorOnExistingEmailAddress() {
    CreateUserDetails createUserDetails = CreateUserDetails.builder()
        .email(EMAIL)
        .name(NAME)
        .password(PASSWORD)
        .build();
    ResponseEntity<Error> response = restTemplate.postForEntity("/users", createUserDetails, Error.class);

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertEquals("Conflict", response.getBody().getError());
    assertEquals("Provided email address already exists.", response.getBody().getMessage());
  }

  @Test
  void createUserErrorOnBadInput() {
    CreateUserDetails createUserDetails = CreateUserDetails.builder()
        .email("test@test.com")
        .name(NAME)
        .password("pwd")
        .build();
    ResponseEntity<Error> response = restTemplate.postForEntity("/users", createUserDetails, Error.class);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Bad Request", response.getBody().getError());
    assertEquals("password: size must be between 8 and 120. ", response.getBody().getMessage());

    createUserDetails = CreateUserDetails.builder()
        .email("test@test.com")
        .name(NAME)
        .build();
    response = restTemplate.postForEntity("/users", createUserDetails, Error.class);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Bad Request", response.getBody().getError());
    assertEquals("password: must not be null. ", response.getBody().getMessage());

    //Combine multiple input validation errors
    createUserDetails = CreateUserDetails.builder()
        .email("@m")
        .password("asd")
        .name(NAME)
        .build();
    response = restTemplate.postForEntity("/users", createUserDetails, Error.class);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Bad Request", response.getBody().getError());
    //Checking with contains because of random message order.
    assertTrue(response.getBody().getMessage().contains("password: size must be between 8 and 120."));
    assertTrue(response.getBody().getMessage().contains("email: size must be between 3 and 254."));
    assertTrue(response.getBody().getMessage().contains("email: must be a well-formed email address."));
  }

  @Test
  void deleteUser() {
    long id = userDao.getCreatedUserId();
    HttpHeaders headers = new HttpHeaders();
    headers.setBasicAuth(EMAIL, PASSWORD);
    HttpEntity<?> entity = new HttpEntity<>(headers);

    ResponseEntity<Void> response = restTemplate.exchange("/users/" + id,
        HttpMethod.DELETE,
        entity,
        Void.class);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    assertTrue(userDao.getUser(id).isEmpty());

    //repeated request should return 401 Unauthorized
    response = restTemplate.exchange("/users/" + id,
        HttpMethod.DELETE,
        entity,
        Void.class);

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
  }

  @Test
  void deleteUserErrorOnDifferentUser() {
    userDao.createUser("test2@test.com", PASSWORD, NAME);
    long id = userDao.getCreatedUserId();

    HttpHeaders headers = new HttpHeaders();
    headers.setBasicAuth(EMAIL, PASSWORD);
    HttpEntity<?> entity = new HttpEntity<>(headers);

    ResponseEntity<Error> response = restTemplate.exchange("/users/" + id,
        HttpMethod.DELETE,
        entity,
        Error.class);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Not authorized or user not found.", response.getBody().getMessage());
    assertEquals("Not Found", response.getBody().getError());
  }

  @Test
  void createProject() {
    long id = userDao.getCreatedUserId();
    CreateProjectDetails createProjectDetails = CreateProjectDetails.builder()
        .name(NEW_PROJECT)
        .build();
    HttpHeaders headers = new HttpHeaders();
    headers.setBasicAuth(EMAIL, PASSWORD);
    HttpEntity<CreateProjectDetails> entity = new HttpEntity<>(createProjectDetails, headers);

    ResponseEntity<Project> response = restTemplate.exchange("/users/" + id + "/projects", HttpMethod.POST, entity, Project.class);
    long projectId = projectDao.getCreatedProjectId();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(Long.toUnsignedString(projectId), response.getBody().getProjectId());
    assertEquals(NEW_PROJECT, response.getBody().getName());
  }

  @Test
  void createProjectErrorOnDifferentUser() {
    userDao.createUser("test2@test.com", PASSWORD, NAME);
    long id = userDao.getCreatedUserId();

    CreateProjectDetails createProjectDetails = CreateProjectDetails.builder()
        .name(NEW_PROJECT)
        .build();
    HttpHeaders headers = new HttpHeaders();
    headers.setBasicAuth(EMAIL, PASSWORD);
    HttpEntity<CreateProjectDetails> entity = new HttpEntity<>(createProjectDetails, headers);

    ResponseEntity<Error> response = restTemplate.exchange("/users/" + id + "/projects", HttpMethod.POST, entity, Error.class);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Not authorized or user not found.", response.getBody().getMessage());
    assertEquals("Not Found", response.getBody().getError());
  }

  @Test
  void listUserProjects() {
    long id = userDao.getCreatedUserId();
    projectDao.createProject(id, NEW_PROJECT);
    long projectId = projectDao.getCreatedProjectId();

    HttpHeaders headers = new HttpHeaders();
    headers.setBasicAuth(EMAIL, PASSWORD);
    HttpEntity<?> entity = new HttpEntity<>(headers);

    ResponseEntity<ProjectCollection> response = restTemplate.exchange("/users/" + id + "/projects", HttpMethod.GET, entity, ProjectCollection.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertFalse(response.getBody().getItems().isEmpty());
    assertEquals(Long.toUnsignedString(projectId), response.getBody().getItems().get(0).getProjectId());
    assertEquals(NEW_PROJECT, response.getBody().getItems().get(0).getName());
  }

  @Test
  void listUserProjectsEmpty() {
    long id = userDao.getCreatedUserId();
    HttpHeaders headers = new HttpHeaders();
    headers.setBasicAuth(EMAIL, PASSWORD);
    HttpEntity<?> entity = new HttpEntity<>(headers);

    ResponseEntity<ProjectCollection> response = restTemplate.exchange("/users/" + id + "/projects", HttpMethod.GET, entity, ProjectCollection.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody().getItems().isEmpty());
  }

  @Test
  void listUserProjectsErrorWhenListingOtherUserProjects() {
    userDao.createUser("test2@test.com", PASSWORD, NAME);
    long id = userDao.getCreatedUserId();
    projectDao.createProject(id, NEW_PROJECT);

    HttpHeaders headers = new HttpHeaders();
    headers.setBasicAuth(EMAIL, PASSWORD);
    HttpEntity<?> entity = new HttpEntity<>(headers);

    ResponseEntity<Error> response = restTemplate.exchange("/users/" + id + "/projects", HttpMethod.GET, entity, Error.class);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Not authorized or user not found.", response.getBody().getMessage());
    assertEquals("Not Found", response.getBody().getError());
  }

  @Test
  void removeUserProjectsOnUserDeletion() {
    long id = userDao.getCreatedUserId();
    projectDao.createProject(id, NEW_PROJECT);

    HttpHeaders headers = new HttpHeaders();
    headers.setBasicAuth(EMAIL, PASSWORD);
    HttpEntity<?> entity = new HttpEntity<>(headers);

    restTemplate.exchange("/users/" + id, HttpMethod.DELETE, entity, Void.class);

    List<Project> projects = projectDao.listProjects(id);

    assertTrue(projects.isEmpty());
  }
}