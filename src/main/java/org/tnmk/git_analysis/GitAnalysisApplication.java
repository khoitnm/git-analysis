package org.tnmk.git_analysis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class GitAnalysisApplication {
  public static void main(String[] args) {
    SpringApplication.run(GitAnalysisApplication.class, args);
  }
}
