package com.javaproject.projects.error;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

/**
 * Custom ErrorAttributes implementation that prevents the internal server information
 * (cause of internal server errors etc.) form leaking to customer error response.
 */
public class InternalServerErrorAttributes extends DefaultErrorAttributes {
  @Override
  public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
    Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, options);
    if ((int)errorAttributes.get("status") >= 500) {
      errorAttributes.put("message", errorAttributes.get("error"));
    }
    return errorAttributes;
  }
}
