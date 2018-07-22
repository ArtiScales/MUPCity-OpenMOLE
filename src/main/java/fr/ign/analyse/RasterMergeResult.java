package fr.ign.analyse;

import java.util.Hashtable;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.geotools.geometry.DirectPosition2D;

/**
 * format of a merged MUP-City output
 * contains few informations about the list of merged raster :
 *  - the number of repetitions of the cells are stored into a Hashtable<DirectPosition2D, Integer> cellRepet 
 *  - the mean of the evaluations of the cells are stored into a Hashtable<DirectPosition2D, Float> cellEval
 *  - a descriptiveStatistic object which im not sure is usefull
 *  - a histogram which is surely more useful, containing the number of  
 * @author mcolomb
 *
 */
public class RasterMergeResult {
	Hashtable<DirectPosition2D, Integer> cellRepet;
	Hashtable<DirectPosition2D, Float> cellEval;
	DescriptiveStatistics histoDS;
	double[] histo;
	double nbScenar;

	public Hashtable<DirectPosition2D, Integer> getCellRepet() {
		return cellRepet;
	}

	public void setCellRepet(Hashtable<DirectPosition2D, Integer> cellRepet) {
		this.cellRepet = cellRepet;
	}

	public Hashtable<DirectPosition2D, Float> getCellEval() {
		return cellEval;
	}

	public void setCellEval(Hashtable<DirectPosition2D, Float> cellEval) {
		this.cellEval = cellEval;
	}

	public DescriptiveStatistics getHistoDS() {
		return histoDS;
	}

	public void setHistoDS(DescriptiveStatistics histoDS) {
		this.histoDS = histoDS;
	}

	public double[] getHisto() {
		return histo;
	}

	public void setHisto(double[] histo) {
		this.histo = histo;
	}

	public double getNbScenar() {
		return nbScenar;
	}
	public void setNbScenar(double nbScenar) {
		this.nbScenar = nbScenar;
	}
}
