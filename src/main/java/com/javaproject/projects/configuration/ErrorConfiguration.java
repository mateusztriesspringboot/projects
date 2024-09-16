package com.javaproject.projects.configuration;

import com.javaproject.projects.error.InternalServerErrorAttributes;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ErrorConfiguration {

  @Bean
  public DefaultErrorAttributes defaultErrorAttributes() {
    return new InternalServerErrorAttributes();
  }
}
