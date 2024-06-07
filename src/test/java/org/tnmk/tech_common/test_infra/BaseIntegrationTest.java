package org.tnmk.tech_common.test_infra;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
public abstract class BaseIntegrationTest {

  @Autowired
  private TestCleanUpService testCleanUpService;
  @AfterEach
  public void cleanUp(){
    testCleanUpService.cleanUpData();
  }
}