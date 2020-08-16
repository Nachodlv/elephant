package com.lab.elephant.controller;

import com.lab.elephant.model.Cube;
import com.lab.elephant.service.CubeServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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
    public void deleteCube_WhenIdExists_ShouldDeleteIt() throws Exception {
        Cube cube = new Cube();
        cube.setName("Cube1");
        Cube cube2 = new Cube();
        cube2.setName("Cube2");

        List<Cube> allCubes = Arrays.asList(cube, cube2);
        given(cubeService.findAllCubes()).willReturn(allCubes);

        mvc.perform(delete("/cube/delete/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
