package com.example.sqLite_demo.service;

import com.example.sqLite_demo.model.Point;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface PointService {
    List<Point> findAllPoints();

    Point addPoint(Point point);

    Optional<Point> findPoint(long id);
    
    void delete(Integer id);
    
    List<Point> getAllByX(Long x);
  
    void save(Point point);
}
