package gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class ScoreGraph extends JPanel implements Serializable{
	public int current = 0;
	BigInteger lastscore = BigInteger.valueOf(-1);
	public BigInteger minScore;
	public BigInteger startingScore;
//	Alignment al;
	public XYSeries points = new XYSeries("Scores");
	public JFreeChart chart;
	public ChartPanel chartPanel;
	NumberAxis yAxis;
	ArrayList<Integer> superlowpoints;
	CustomRenderer cr;
	ArrayList<BigInteger> scoreHistory;
	
	class CustomRenderer extends StandardXYItemRenderer
	{
		  public Paint getItemPaint(int series, int item) {

    	if(superlowpoints.contains(item))
    	{
    		return Color.red;
    		
    	}
    	return Color.blue;
 }
	}
	public ScoreGraph() {
		super();
		cr = new CustomRenderer();
		scoreHistory = new ArrayList<BigInteger>();
		//this.chart =
	superlowpoints = new ArrayList<Integer>();
		XYSeriesCollection data = new XYSeriesCollection(points);
       this.chart = ChartFactory.createXYLineChart(
                null,
                null, 
                null, 
                data,
                PlotOrientation.VERTICAL,
                false,
                false,
                false
            );

	
		current = 0;
		chartPanel = new ChartPanel(chart);
	   
		this.setLayout(new BorderLayout());
		this.add(chartPanel);
		XYPlot plot = chart.getXYPlot();
		yAxis = (NumberAxis) plot.getRangeAxis();
		yAxis.setAutoRangeIncludesZero(false);
		yAxis.setAutoRange(false);
		yAxis.setVisible(false);
		((NumberAxis) plot.getDomainAxis()).setVisible(false);
		this.setPreferredSize(new Dimension(200,70 -2));
		this.setMaximumSize(this.getPreferredSize());
		plot.setRenderer(cr);
		plot.getRenderer().setSeriesStroke(0, new BasicStroke(2f));
		
		 chartPanel.setToolTipText("Shows the progression of the score. Note that the y-axis starts at the initial score of the alignment, as it was loaded");
		
		// TODO Auto-generated constructor stub
	}
	
	public void addScore(BigInteger newscore)
	{
		scoreHistory.add(newscore);
		if(minScore!=null)
		if(newscore.compareTo(minScore) ==-1)
		{
			superlowpoints.add(current);
			newscore = lastscore;
		}
		points.add(new XYDataItem(current, newscore.subtract(startingScore)));
		current++;
		yAxis.setRangeWithMargins(minScore.subtract(startingScore).intValue(), points.getMaxY());
		lastscore =  newscore;

		
	}
	
	public void printHistory()
	{
		for(int i = 0; i < scoreHistory.size(); i++)
		{
			System.out.print(scoreHistory.get(i).toString()+">");
		}
	}

}
