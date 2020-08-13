package com.example.sqLite_demo.service;

import com.example.sqLite_demo.model.Point;
import com.example.sqLite_demo.repository.PointRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PointServiceImpl implements PointService {
  private final PointRepository pointRepository;
  
  public PointServiceImpl(PointRepository pointRepository) {
    this.pointRepository = pointRepository;
  }
  
  @Override
  public List<Point> findAllPoints() {
    return pointRepository.findAll();
  }
  
  @Override
  public Point addPoint(Point point) {
    return pointRepository.save(point);
  }
  
  @Override
  public Optional<Point> findPoint(long id) {
    return pointRepository.findById(id);
  }
  
  @Override
  public void delete(Integer id) {
  
  }
  
  @Override
  public List<Point> getAllByX(Long x) {
    return pointRepository.findAllByX(x);
  }
  
  @Override
  public void save(Point point) {
    pointRepository.save(point);
  }
  
}
