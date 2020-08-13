package com.example.sqLite_demo.controller;

import com.example.sqLite_demo.model.Point;
import com.example.sqLite_demo.service.PointService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/point")
public class PointController {
  private final PointService pointService;
  
  public PointController(PointService pointService) {
    this.pointService = pointService;
  }
  
  @GetMapping(path = "/getByX/{x}")
  public List<Point> getPointsByX(@PathVariable("x") Long x) {
    return pointService.getAllByX(x);
  }
  
}
