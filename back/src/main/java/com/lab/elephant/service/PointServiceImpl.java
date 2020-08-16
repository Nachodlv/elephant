package com.lab.elephant.service;

import com.lab.elephant.model.Point;
import com.lab.elephant.repository.PointRepository;
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
    public Optional<Point> findPoint(long id) {
        return pointRepository.findById(id);
    }

    @Override
    public List<Point> findAllPointsByX(long x) {
        return pointRepository.findAllByX(x);
    }
}
