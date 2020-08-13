package com.example.sqLite_demo.controller;

import com.example.sqLite_demo.model.Point;
import com.example.sqLite_demo.service.PointServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.ArrayList;
import java.util.List;

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
  public void getAllByX_WhenHavingMultiplePoints_ShouldReturnTheOnesWithTheCorrectX_onController() throws Exception {
  
    Point point = new Point();
    point.setX(0L);
    point.setY(0L);
    Point point2 = new Point();
    point2.setX(1L);
    point2.setY(2L);
    Point point3 = new Point();
    point3.setX(0L);
    point3.setY(1L);
  
    pointService.save(point);
    pointService.save(point2);
    pointService.save(point3);
  
    List<Point> points = new ArrayList<>();
    // aca podria usar el metodo del repo?? tipo todo esto no es test de mas?
    // ya que el controller llama al service que llama al repo que ya esta testeado.
    points.add(point2);
    given(pointService.getAllByX(1L)).willReturn(points);
    mvc.perform(get("/point/getByX/1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].x", is((int) (long) point2.getX())))
            .andDo(MockMvcResultHandlers.print());
  }
  //todo este casteo es bien raro preguntar!
}
