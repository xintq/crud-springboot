/*
 * Copyright (c) K.X(Kevin Xin) 2017.
 * Find more details in http://xintq.net
 *
 */

package com.example.crud.util;

import java.util.List;

public class ChartsResponse {
	private String chartName;
	private List<String> labels;
	private List<Dataset> datasets;

	public String getChartName() {
		return chartName;
	}

	public void setChartName(String chartName) {
		this.chartName = chartName;
	}

	public List<String> getLabels() {
		return labels;
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}

	public List<Dataset> getDatasets() {
		return datasets;
	}

	public void setDatasets(List<Dataset> datasets) {
		this.datasets = datasets;
	}

}
