package com.lab.elephant.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.lab.elephant.model.Cube;
import org.springframework.stereotype.Repository;

@Repository
public interface CubeRepository extends JpaRepository<Cube, Long> {
}
