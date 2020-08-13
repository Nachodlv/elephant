package com.example.sqLite_demo.controller;

import com.example.sqLite_demo.model.Cube;
import com.example.sqLite_demo.model.Point;
import com.example.sqLite_demo.service.CubeServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CubeController.class)
public class CubeControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private CubeServiceImpl cubeService;

    @Test
    public void getCubes_WhenCubesAdded_ShouldReturnCubes() throws Exception {
        Cube cube = new Cube();
        cube.setName("Cube1");
        Cube cube2 = new Cube();
        cube2.setName("Cube2");
        List<Cube> allCubes = Arrays.asList(cube, cube2);

        given(cubeService.findAllCubes()).willReturn(allCubes);

        mvc.perform(get("/cube/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is(cube.getName())))
                .andExpect(jsonPath("$[1].name", is(cube2.getName())));
    }

    @Test
    public void getCube_WhenIdDoesNotExists_ShouldReturnNotFound() throws Exception {

        given(cubeService.findCube(1)).willReturn(Optional.empty());

        mvc.perform(get("/cube/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    @Test
    public void deleteCubeWithPoints() throws Exception {
        Cube cube = new Cube();
        cube.setName("Cube1");
        
        List<Point> points = new ArrayList<>();
        Point point1 = new Point();
        Point point2 = new Point();
        point1.setCube(cube);
        point2.setCube(cube);
        point1.setX(1L);
        point2.setX(1L);
        point1.setY(1L);
        point2.setY(2L);
        points.add(point1);
        points.add(point2);
        cube.setPoints(points);
        
        given(cubeService.findCube(1)).willReturn(Optional.of(cube));
        mvc.perform(delete("/cube/delete/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
