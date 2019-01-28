package eniac.automatichandler;
import java.awt.Color;
import java.util.Date;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;

/**
 * Classe permettant d'afficher graphiquement le temps moyens d'exécution des requêtes.
 * Nous utilisons la librairie JFreeChart afin de pouvoir afficher et mettre à jours simplement les données.
 * La barre horizontale bleu correspond à la moyenne qu'on cherche à obtenir et les barres vertes
 * correpondent à la tolérance d'écart accepté avant de faire une action.
 * @author L-C
 *
 */

public class ComputeTimeCharts extends ApplicationFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private XYDataset dataset;
	private TimeSeries series;
	public ComputeTimeCharts(String name, double average) {
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
		chartPanel.setPreferredSize( new java.awt.Dimension( 900 , 480 ) );         
		chartPanel.setMouseZoomable( true , false );    
		ValueMarker marker = new ValueMarker(average);
		marker.setPaint(Color.BLUE);
		((XYPlot)chart.getPlot()).addRangeMarker(marker);
		ValueMarker markerb = new ValueMarker(average-1000);
		markerb.setPaint(Color.GREEN);
		((XYPlot)chart.getPlot()).addRangeMarker(markerb);
		ValueMarker markert = new ValueMarker(average+2000);
		markert.setPaint(Color.GREEN);
		((XYPlot)chart.getPlot()).addRangeMarker(markert);
		setContentPane( chartPanel );	
	}
	
	/**
	 * Ajoute la nouvelle moyenne dans le graphe et mets ainsi à jours les données
	 * @param x la nouvelle moyenne calculé l'instant t
	 */
	public void addData(double x) {
		series.addOrUpdate(new Second(new Date()) , (double)x);
	}
	

}
