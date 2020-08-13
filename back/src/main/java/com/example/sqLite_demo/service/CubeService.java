package com.example.sqLite_demo.service;

import com.example.sqLite_demo.model.Cube;
import com.example.sqLite_demo.model.Point;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface CubeService {
    List<Cube> findAllCubes();

    Cube addCube(Cube cube);

    Optional<Cube> findCube(long id);

    Optional<Cube> addPointToCube(long cubeId, Point point);
  
  void delete(Integer id);
}
