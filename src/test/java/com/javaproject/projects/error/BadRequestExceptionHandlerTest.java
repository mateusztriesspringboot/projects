package com.javaproject.projects.error;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;

class BadRequestExceptionHandlerTest {

  private BadRequestExceptionHandler badRequestExceptionHandler;
  private HttpServletRequest httpServletRequestMock;
  private MethodArgumentNotValidException methodArgumentNotValidExceptionMock;

  @BeforeEach
  void setUp() {
    badRequestExceptionHandler = new BadRequestExceptionHandler();
    httpServletRequestMock = Mockito.mock(HttpServletRequest.class);
    methodArgumentNotValidExceptionMock = Mockito.mock(MethodArgumentNotValidException.class);
  }

  @Test
  void handleValidationExceptions() {
    BindingResult bindingResultMock = Mockito.mock(BindingResult.class);
    when(methodArgumentNotValidExceptionMock.getBindingResult()).thenReturn(bindingResultMock);
    when(bindingResultMock.getFieldErrors()).thenReturn(List.of(new FieldError("password", "password", "password should be between 8 and 120 characters")));

    ResponseEntity<Map<String, Object>> response = badRequestExceptionHandler.handleValidationExceptions(methodArgumentNotValidExceptionMock, httpServletRequestMock);

    Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Assertions.assertEquals(400, response.getBody().get("status"));
    Assertions.assertEquals("Bad Request", response.getBody().get("error"));
    Assertions.assertEquals("password: password should be between 8 and 120 characters. ", response.getBody().get("message"));
    Assertions.assertNotNull(response.getBody().get("timestamp"));
  }
}