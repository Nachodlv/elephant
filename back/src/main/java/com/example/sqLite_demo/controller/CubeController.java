package com.example.sqLite_demo.controller;

import com.example.sqLite_demo.model.Cube;
import com.example.sqLite_demo.model.Point;
import com.example.sqLite_demo.service.CubeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/cube")
public class CubeController {
	
	private final CubeService cubeService;

	public CubeController(CubeService cubeService) {
		this.cubeService = cubeService;
	}

	@GetMapping("/all")
	public List<Cube> getCubes() {
		return cubeService.findAllCubes();
	}

	@GetMapping("{id}")
	public Cube getCube(@PathVariable("id") long id) {
		Optional<Cube> cube = cubeService.findCube(id);
		if(cube.isPresent()) return cube.get();
		throw new ResponseStatusException(
				HttpStatus.NOT_FOUND, "Cube Not Found");
	}

	@PostMapping()
	public Cube addCube(@RequestBody Cube cube) {
		return cubeService.addCube(cube);
	}

	@PutMapping(path = "/{id}/point")
	public Cube addPoint(@PathVariable("id") long id, @RequestBody Point point) {
		Optional<Cube> optionalCube = cubeService.addPointToCube(id, point);
		if(optionalCube.isPresent()) {
			return optionalCube.get();
		}
		throw new ResponseStatusException(
				HttpStatus.NOT_FOUND, "Cube Not Found");
	}
	@DeleteMapping(path = "/delete/{id}")
	public void deletePoint(@PathVariable("id") Integer id) {
		cubeService.delete(id);
	}
}
