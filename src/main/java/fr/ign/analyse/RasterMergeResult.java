package fr.ign.analyse;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.geotools.geometry.DirectPosition2D;

/**
 * format of a merged MUP-City output contains few informations about the list of merged raster : - the number of repetitions of the cells are stored into a
 * Hashtable<DirectPosition2D, Integer> cellRepet - the mean of the evaluations of the cells are stored into a Hashtable<DirectPosition2D, Float> cellEval - a descriptiveStatistic
 * object which im not sure is usefull - a histogram which is surely more useful, containing the number of
 * 
 * @author mcolomb
 *
 */
public class RasterMergeResult {
	Hashtable<DirectPosition2D, Integer> cellRepet;
	Hashtable<DirectPosition2D, Float> cellEval;
	Hashtable<DirectPosition2D, ArrayList<Float>> cellEvals;
	DescriptiveStatistics histoDS;
	double[] histo;
	double nbScenar;

	/**
	 * if the raster to be merged are too big, merge into separate RaserMergeResult and unite them with this method I know this is a bit ugly (better use steam, mais bon..) TODO
	 * pour l'instant, ne merge que le paramètre cellEvals. à faire pour les autres
	 * 
	 * @param list
	 *            des RaserMergeResult
	 * @return le RaserMergeResult unifié
	 */
	public static RasterMergeResult merge(List<RasterMergeResult> list) {
		RasterMergeResult finale = new RasterMergeResult();

		Hashtable<DirectPosition2D, Float> cellEvalFinal = new Hashtable<DirectPosition2D, Float>();
		Hashtable<DirectPosition2D, ArrayList<Float>> cellEvalsFinal = new Hashtable<DirectPosition2D, ArrayList<Float>>();

		for (RasterMergeResult single : list) {
			Hashtable<DirectPosition2D, ArrayList<Float>> cellevalsSingle = single.getCellEvals();
			for (DirectPosition2D dirpos : cellevalsSingle.keySet()) {
				ArrayList<Float> tmp = cellevalsSingle.get(dirpos);
				if (!cellEvalsFinal.isEmpty() && cellEvalFinal.contains(dirpos)) {
					tmp.addAll(cellEvalsFinal.get(dirpos));
				}
				cellEvalsFinal.put(dirpos, tmp);
			}
		}
		return finale;

	}

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

	public Hashtable<DirectPosition2D, ArrayList<Float>> getCellEvals() {
		return this.cellEvals;
	}

	public void setCellEvals(Hashtable<DirectPosition2D, ArrayList<Float>> cellevals) {
		this.cellEvals = cellevals;
	}

}
