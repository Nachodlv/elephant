package com.lab.elephant.service;

import java.util.List;
import java.util.Optional;

import com.lab.elephant.repository.PointRepository;
import com.lab.elephant.model.Point;
import org.springframework.stereotype.Service;

import com.lab.elephant.repository.CubeRepository;
import com.lab.elephant.model.Cube;

@Service
public class CubeServiceImpl implements CubeService {

	private final CubeRepository cubeRepository;
	private final PointRepository pointRepository;

	public CubeServiceImpl(CubeRepository cubeRepository, PointRepository pointRepository) {
		this.cubeRepository = cubeRepository;
		this.pointRepository = pointRepository;
	}

	@Override
	public List<Cube> findAllCubes() {
		return cubeRepository.findAll();
	}

	@Override
	public Cube addCube(Cube cube) {
		return cubeRepository.save(cube);
	}

	@Override
	public Optional<Cube> findCube(long id) {
		return cubeRepository.findById(id);
	}

	@Override
	public Optional<Cube> addPointToCube(long cubeId, Point point) {
		Optional<Cube> optionalCube = cubeRepository.findById(cubeId);
		if(optionalCube.isPresent()) {
			Cube cube = optionalCube.get();
			point.setCube(cube);
			cube.getPoints().add(point);
			pointRepository.save(point);
			return Optional.of(cube);
		}
		return Optional.empty();
 	}

	@Override
	public void deleteCube(long id) {
	    if (findCube(id).isPresent())
	        cubeRepository.deleteById(id);
	}

}
