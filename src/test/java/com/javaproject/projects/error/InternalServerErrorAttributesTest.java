package com.javaproject.projects.error;

import jakarta.servlet.RequestDispatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class InternalServerErrorAttributesTest {

  private InternalServerErrorAttributes internalServerErrorAttributes;
  private WebRequest webRequestMock;
  @BeforeEach
  void setUp() {
    internalServerErrorAttributes = new InternalServerErrorAttributes();
    webRequestMock = Mockito.mock(WebRequest.class);
  }

  @Test
  void getErrorAttributes() {
    when(webRequestMock.getAttribute(RequestDispatcher.ERROR_STATUS_CODE, RequestAttributes.SCOPE_REQUEST))
        .thenReturn(400);

    Map<String, Object> attributes = internalServerErrorAttributes.getErrorAttributes(webRequestMock, ErrorAttributeOptions.defaults());

    assertEquals(400, attributes.get("status"));
    assertEquals("Bad Request", attributes.get("error"));
  }

  @Test
  void getErrorAttributesWhenInternalServerError() {
    when(webRequestMock.getAttribute(RequestDispatcher.ERROR_STATUS_CODE, RequestAttributes.SCOPE_REQUEST))
        .thenReturn(500);

    Map<String, Object> attributes = internalServerErrorAttributes.getErrorAttributes(webRequestMock, ErrorAttributeOptions.defaults());

    assertEquals(500, attributes.get("status"));
    assertEquals("Internal Server Error", attributes.get("error"));
    assertEquals("Internal Server Error", attributes.get("message"));
  }
}