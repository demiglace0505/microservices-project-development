package com.demiglace.location.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.springframework.stereotype.Component;

@Component
public class ReportUtilImpl implements ReportUtil {

	@Override
	public void generatePieChart(String path, List<Object[]> data) {
		// copy the data into the pie data set
		DefaultPieDataset dataset = new DefaultPieDataset();
		for (Object[] objects: data) {
			// key, value
			dataset.setValue(objects[0].toString(), new Double(objects[1].toString()));
		}
		// create chart object
		JFreeChart chart = ChartFactory.createPieChart3D("Location Report", dataset);
		
		// convert to image
		try {
			ChartUtilities.saveChartAsJPEG(new File(path+"/pieChart.jpeg"), chart, 300, 300);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
