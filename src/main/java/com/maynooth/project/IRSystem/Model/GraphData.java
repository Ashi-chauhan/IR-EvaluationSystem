package com.maynooth.project.IRSystem.Model;

import java.util.ArrayList;
import java.util.List;

public class GraphData {
	String name;
	List<Double> data = new ArrayList<>();
	
	public GraphData() {
		super();
	}

	public GraphData(String name, List<Double> data) {
		super();
		this.name = name;
		this.data = data;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Double> getData() {
		return data;
	}

	public void setData(List<Double> data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "GraphData [name=" + name + ", data=" + data + "]";
	}
	
}
