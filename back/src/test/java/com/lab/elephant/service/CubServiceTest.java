package com.lab.elephant.service;

import com.lab.elephant.model.Cube;
import com.lab.elephant.model.Point;
import com.lab.elephant.repository.CubeRepository;
import com.lab.elephant.repository.PointRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class CubServiceTest {

    @TestConfiguration
    static class CubeServiceImplTestContextConfiguration {
        @Autowired
        private CubeRepository cubeRepository;
        @Autowired
        private PointRepository pointRepository;

        @Bean
        public CubeService employeeService() {
            return new CubeServiceImpl(cubeRepository, pointRepository);
        }
    }

    @Autowired
    private CubeService cubeService;
    @MockBean
    private CubeRepository cubeRepository;
    @MockBean
    private PointRepository pointRepository;

    @Test
    public void AddPointToCube_WhenAddingANewPoint_ShouldReturnACubeWithThePoint() {
        String cubeName = "Cubo";
        Cube cube = new Cube();
        cube.setName(cubeName);
        Point point = new Point();
        point.setX(1L);
        Mockito.when(pointRepository.save(point)).thenReturn(point);
        Mockito.when(cubeRepository.findById(1L)).thenReturn(Optional.of(cube));

        Optional<Cube> optionalCube = cubeService.addPointToCube(1L, point);

        assertThat(optionalCube.isPresent()).isTrue();
        assertThat(optionalCube.get().getName()).isEqualTo(cubeName);
        assertThat(optionalCube.get().getPoints().size()).isEqualTo(1);
        assertThat(optionalCube.get().getPoints().get(0)).isEqualTo(point);
    }

    @Test
    public void DeleteCube_WhenExistingIt_ShouldBeDeleted() {
        Cube cube = new Cube();
        cube.setName("Cubo");

        Mockito.when(cubeRepository.findById(0L)).thenReturn(Optional.of(cube));

        Optional<Cube> optionalCube = cubeService.findCube(0L);
        assertThat(optionalCube.isPresent()).isTrue();

        cubeService.deleteCube(cube.getId());

        assertThat(cubeService.findAllCubes().contains(cube)).isFalse();
    }
}
