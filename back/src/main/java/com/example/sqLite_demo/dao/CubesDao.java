package com.example.sqLite_demo.dao;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;

import com.example.sqLite_demo.model.Cubes;

@Transactional
public interface CubesDao extends CrudRepository<Cubes, Long>{

}
