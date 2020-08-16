package com.lab.elephant.controller;

import com.lab.elephant.model.Point;
import com.lab.elephant.service.PointService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/point")
public class PointController {

    private final PointService pointService;

    public PointController(PointService pointService) {
        this.pointService = pointService;
    }

    @GetMapping("/all")
    public List<Point> getCubes() {
        return pointService.findAllPoints();
    }

    @GetMapping("/{id}")
    public Point getPoint(@PathVariable("id") long id) {
        Optional<Point> point = pointService.findPoint(id);
        if (point.isPresent()) return point.get();
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Cube Not Found");
    }

    @GetMapping("/byX/{x}")
    public List<Point> getPointsByX(@PathVariable("x") long x){
        return pointService.findAllPointsByX(x);
    }
}
