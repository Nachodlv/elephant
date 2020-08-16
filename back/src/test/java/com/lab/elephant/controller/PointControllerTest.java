package com.lab.elephant.controller;

import com.lab.elephant.model.Point;
import com.lab.elephant.service.PointServiceImpl;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PointController.class)
public class PointControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private PointServiceImpl pointService;

    @Test
    public void getPoints_WhenPointsAdded_ShouldReturnPoints() throws Exception {
        Point point = new Point();
        point.setX(1L);
        point.setY(2L);
        Point point2 = new Point();
        point2.setX(4L);
        point2.setY(3L);
        List<Point> allPoints = Arrays.asList(point, point2);

        given(pointService.findAllPoints()).willReturn(allPoints);

        mvc.perform(get("/point/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].x", is((int) (long) point.getX())))
                .andExpect(jsonPath("$[0].y", is((int) (long) point.getY())))
                .andExpect(jsonPath("$[1].x", is((int) (long) point2.getX())))
                .andExpect(jsonPath("$[1].y", is((int) (long) point2.getY())));
    }

    @Test
    public void getPoint_WhenIdDoesNotExists_ShouldReturnNotFound() throws Exception {

        given(pointService.findPoint(1)).willReturn(Optional.empty());

        mvc.perform(get("/point/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getPointsByX_WhenPointsAdded_ShouldReturnPointsWithThatX() throws Exception {
        Point point = new Point();
        point.setX(0L);
        point.setY(0L);
        Point point2 = new Point();
        point2.setX(1L);
        point2.setY(2L);
        Point point3 = new Point();
        point3.setX(0L);
        point3.setY(1L);

        List<Point> allPointsWithX = Arrays.asList(point, point3);

        given(pointService.findAllPointsByX(0L)).willReturn(allPointsWithX);

        mvc.perform(get("/point/byX/0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].x", is(0)))
                .andExpect(jsonPath("$[0].y", is(0)))
                .andExpect(jsonPath("$[1].x", is(0)))
                .andExpect(jsonPath("$[1].y", is(1)));
    }
}
