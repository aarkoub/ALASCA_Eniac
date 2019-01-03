package eniac.automatichandler;
import java.util.Date;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class ComputeTimeCharts extends ApplicationFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private XYDataset dataset;
	private TimeSeries series;
	public ComputeTimeCharts(String name) {
		super("Graphe " + name);
		series = new TimeSeries("Temps moyen de calcul");
		dataset = new TimeSeriesCollection(series);
		JFreeChart chart = ChartFactory.createTimeSeriesChart("Temps de calcul moyen pour l'application " + name,
				"Temps (en seconde hh:mm:ss)",
				"Temps moyens de calcul (en milliseconde ms)",
				dataset,
				false,
				false,
				false);
		ChartPanel chartPanel = new ChartPanel( chart );         
		chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 370 ) );         
		chartPanel.setMouseZoomable( true , false );         
		setContentPane( chartPanel );	
	}
	
	public void addData(double x) {
		series.addOrUpdate(new Second(new Date()) , (double)x);
	}
	
	public static void main(String[] args) throws InterruptedException {
		String name = "nom";
		ComputeTimeCharts chart = new ComputeTimeCharts(name);
		chart.pack();
		RefineryUtilities.positionFrameRandomly(chart);
		chart.setVisible(true);
		
		for(int i = 1; i < 100; i++) {
			chart.addData(Math.random()*400);
			Thread.sleep(1000);
		}

	}

}
