package com.example.sqLite_demo.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sqLite_demo.model.Cube;
import org.springframework.stereotype.Repository;

@Repository
public interface CubeRepository extends JpaRepository<Cube, Long> {
}
