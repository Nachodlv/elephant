package com.lab.elephant.service;

import com.lab.elephant.model.Cube;
import com.lab.elephant.model.Point;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface CubeService {
    List<Cube> findAllCubes();

    Cube addCube(Cube cube);

    Optional<Cube> findCube(long id);

    Optional<Cube> addPointToCube(long cubeId, Point point);

    void deleteCube(long id);
}
