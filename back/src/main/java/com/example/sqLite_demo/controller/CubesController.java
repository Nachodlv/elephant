package com.example.sqLite_demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sqLite_demo.model.Cubes;
import com.example.sqLite_demo.service.CubesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping(value = "/")
public class CubesController {
	
	@Autowired
	private CubesService cubesService;
	
	@GetMapping(path = "/cubes")
	public String getCubes() throws JsonProcessingException {
		List<Cubes> cubes= cubesService.findAllCubes();
		return new ObjectMapper().writeValueAsString(cubes);
	}
	

}
