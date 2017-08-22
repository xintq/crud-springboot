package com.example.crud.util;

import java.util.List;

public class Dataset {

	private List<Integer> data;
	private String label;
	private String fillColor;
	private String strokeColor;
	private int borderWidth = 1;
	private List<String> backgroundColor;
	private List<String> borderColor;

	public String getFillColor() {
		return fillColor;
	}

	public void setFillColor(String fillColor) {
		this.fillColor = fillColor;
	}

	public String getStrokeColor() {
		return strokeColor;
	}

	public void setStrokeColor(String strokeColor) {
		this.strokeColor = strokeColor;
	}

	public int getBorderWidth() {
		return borderWidth;
	}

	public void setBorderWidth(int borderWidth) {
		this.borderWidth = borderWidth;
	}

	public List<String> getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(List<String> backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public List<String> getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(List<String> borderColor) {
		this.borderColor = borderColor;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<Integer> getData() {
		return data;
	}

	public void setData(List<Integer> data) {
		this.data = data;
	}
}
