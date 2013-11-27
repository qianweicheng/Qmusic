package com.qmusic.uitls;

import java.util.List;

import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.chart.XYChart;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint.Align;

import com.androidquery.util.AQUtility;

public class AChartHelper {
	private static final int SERIES_NR = 1;

	public static final GraphicalView getGraphicalView(Activity ctx, List<Double> datas) {
		XYMultipleSeriesRenderer renderer = getBarRenderer(ctx);
		XYMultipleSeriesDataset dataset = getBarDataset(renderer, datas);
		XYChart chart = new BarChart(dataset, renderer, Type.STACKED);
		GraphicalView graphicalView1 = new GraphicalView(ctx, chart);
		return graphicalView1;
	}

	static final XYMultipleSeriesRenderer getBarRenderer(Context ctx) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer.setChartTitleTextSize(AQUtility.dip2pixel(ctx, 18));
		// renderer.setAxesColor(txtColor);
		renderer.setAxisTitleTextSize(AQUtility.dip2pixel(ctx, 14));
		// renderer.setLabelsColor(txtColor);
		renderer.setLabelsTextSize(AQUtility.dip2pixel(ctx, 14));
		// renderer.setLegendHeight(0);
		// renderer.setLegendTextSize(0);
		renderer.setShowLegend(false);
		renderer.setBarSpacing(1);
		renderer.setBarWidth(AQUtility.dip2pixel(ctx, 15));
		renderer.setZoomEnabled(false, false);
		int margin = AQUtility.dip2pixel(ctx, 38);
		int margin2 = AQUtility.dip2pixel(ctx, 20);
		renderer.setMargins(new int[] { margin, margin, 0, margin2 });
		renderer.setShowGridX(true);
		renderer.setGridColor(0x66666666);
		// renderer.setFitLegend(true);
		renderer.setYLabelsAlign(Align.RIGHT);
		renderer.setYLabelsPadding(AQUtility.dip2pixel(ctx, 3));
		// renderer.setApplyBackgroundColor(true);
		// renderer.setBackgroundColor(Color.YELLOW);
		renderer.setMarginsColor(5);
		XYSeriesRenderer r = new XYSeriesRenderer();
		r.setColor(0xff000000);
		renderer.addSeriesRenderer(r);
		// r = new XYSeriesRenderer();
		// r.setColor(Color.GREEN);
		// renderer.addSeriesRenderer(r);
		return renderer;
	}

	static final XYMultipleSeriesDataset getBarDataset(XYMultipleSeriesRenderer renderer, List<Double> datas) {
		// renderer.setXTitle("x-title");
		renderer.setYTitle("y-title");
		renderer.setXAxisMin(0.5);
		renderer.setXAxisMax(10.5);
		renderer.setYAxisMin(0);
		renderer.setYAxisMax(12);
		renderer.setXLabels(10);
		renderer.setYLabels(4);
		renderer.setPanLimits(new double[] { 0.5, datas.size() + 0.5, 0, 12 });
		renderer.setPanEnabled(datas.size() > 10);
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		final int nr = datas.size();
		for (int i = 0; i < SERIES_NR; i++) {
			CategorySeries series = new CategorySeries("日期");
			for (int k = 0; k < nr; k++) {
				series.add(datas.get(k));
			}
			dataset.addSeries(series.toXYSeries());
		}

		return dataset;
	}
}
