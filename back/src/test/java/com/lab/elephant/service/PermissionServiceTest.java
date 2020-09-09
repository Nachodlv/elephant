package com.lab.elephant.service;

import com.lab.elephant.repository.PermissionRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class PermissionServiceTest {
  @TestConfiguration
  static class PermissionServiceTestContextConfiguration {
    private PermissionRepository permissionRepository;
    @Bean
    public PermissionService permissionService() {
      return new PermissionServiceImpl(permissionRepository);
    }
  }
  
  @Autowired
  private PermissionService permissionService;
  
  @Test
  public void implementTest() {
    //todo implement permissionService Test
    System.out.println("TODO");
  }
  
}
