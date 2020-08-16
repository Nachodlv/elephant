package com.lab.elephant.service;

import com.lab.elephant.model.Point;
import com.lab.elephant.repository.PointRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class PointServiceTest {

    @TestConfiguration
    static class CubeServiceImplTestContextConfiguration {
        @Autowired
        private PointRepository pointRepository;

        @Bean
        public PointService employeeService() {
            return new PointServiceImpl(pointRepository);
        }
    }
    @Autowired
    private PointService pointService;
    @MockBean
    private PointRepository pointRepository;

    @Test
    public void AddPoints_WhenFindingAllPointsByX_ShouldReturnAllThatPoints() {
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
        pointRepository.saveAll(Arrays.asList(point, point2, point3));

        Mockito.when(pointService.findAllPointsByX(0L)).thenReturn(allPointsWithX);

        List<Point> allPointsByX = pointService.findAllPointsByX(0L);

        assertThat(allPointsByX).doesNotContain(point2);
        assertThat(allPointsByX.size()).isEqualTo(2);

    }
}
