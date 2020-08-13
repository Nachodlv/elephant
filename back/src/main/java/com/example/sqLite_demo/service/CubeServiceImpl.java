package com.example.sqLite_demo.service;

import com.example.sqLite_demo.model.Cube;
import com.example.sqLite_demo.model.Point;
import com.example.sqLite_demo.repository.CubeRepository;
import com.example.sqLite_demo.repository.PointRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CubeServiceImpl implements CubeService {

	private final CubeRepository cubeRepository;
	private final PointRepository pointRepository;

	public CubeServiceImpl(CubeRepository cubeRepository, PointRepository pointRepository) {
		this.cubeRepository = cubeRepository;
		this.pointRepository = pointRepository;
	}

	public List<Cube> findAllCubes() {
		return cubeRepository.findAll();
	}

	public Cube addCube(Cube cube) {
		return cubeRepository.save(cube);
	}

	public Optional<Cube> findCube(long id) {
		return cubeRepository.findById(id);
	}

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
	public void delete(Integer id) {
    findCube(id).ifPresent(cubeRepository::delete);
  }
}
