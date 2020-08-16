package com.lab.elephant.service;

import com.lab.elephant.model.Point;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface PointService {
    List<Point> findAllPoints();

    Optional<Point> findPoint(long id);

    List<Point> findAllPointsByX(long x);
}
