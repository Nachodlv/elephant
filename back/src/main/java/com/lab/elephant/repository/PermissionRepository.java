package com.lab.elephant.repository;

import com.lab.elephant.model.Permission;
import com.lab.elephant.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

  List<Permission> findAllByUser(User user);

}
