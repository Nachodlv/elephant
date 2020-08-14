package com.lab.elephant.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="cube")
public class Cube {
	
	@Id
	@GeneratedValue
	private long id;
	
	private String name;
	
	private String description;
	
	@Column(name = "create_date")
	private String createDate;

	@OneToMany(mappedBy = "cube", cascade = CascadeType.PERSIST)
	private List<Point> points = new ArrayList<>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String create_date) {
		this.createDate = create_date;
	}

	public List<Point> getPoints() {
		return points;
	}

	public void setPoints(List<Point> points) {
		this.points = points;
	}
}
