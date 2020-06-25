package com.example.sqLite_demo.model;

import javax.persistence.*;

@Entity
@Table(name="cubes")
public class Cubes {
	
	@Id
	private String id;
	
	private Integer account_id;
	
	private String name;
	
	private String description;
	
	private String cube_schema;
	
	private String source_file;
	
	private String create_date;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getAccount_id() {
		return account_id;
	}

	public void setAccount_id(Integer account_id) {
		this.account_id = account_id;
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

	public String getCube_schema() {
		return cube_schema;
	}

	public void setCube_schema(String cube_schema) {
		this.cube_schema = cube_schema;
	}

	public String getSource_file() {
		return source_file;
	}

	public void setSource_file(String source_file) {
		this.source_file = source_file;
	}

	public String getCreate_date() {
		return create_date;
	}

	public void setCreate_date(String create_date) {
		this.create_date = create_date;
	}
	
	

}
