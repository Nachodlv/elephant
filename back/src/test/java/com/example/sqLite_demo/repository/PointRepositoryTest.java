package com.example.sqLite_demo.repository;

import com.example.sqLite_demo.model.Point;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import static org.assertj.core.api.Assertions.assertThat;


import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PointRepositoryTest {

    @Autowired
    private PointRepository pointRepository;

    @Test
    public void findAllByX_WhenHavingMultiplePoints_ShouldReturnTheOnesWithTheCorrectX() {
        Point point = new Point();
        point.setX(0L);
        point.setY(0L);
        Point point2 = new Point();
        point2.setX(1L);
        point2.setY(2L);
        Point point3 = new Point();
        point3.setX(0L);
        point3.setY(1L);

        pointRepository.save(point);
        pointRepository.save(point2);
        pointRepository.save(point3);

        List<Point> points = pointRepository.findAllByX(0L);

        assertThat(points.size()).isEqualTo(2);
        assertThat(points.get(0).getId()).isEqualTo(point.getId());
        assertThat(points.get(1).getId()).isEqualTo(point3.getId());
    }
}
