package com.maynooth.project.IRSystem.Model;

public class TableData {

	String value;
	boolean maxValue;
	boolean minValue;
	
	public TableData() {
		super();
	}
	public TableData(String value) {
		super();
		this.value = value;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public boolean isMaxValue() {
		return maxValue;
	}
	public void setMaxValue(boolean maxValue) {
		this.maxValue = maxValue;
	}
	public boolean isMinValue() {
		return minValue;
	}
	public void setMinValue(boolean minValue) {
		this.minValue = minValue;
	}
	
	
}
