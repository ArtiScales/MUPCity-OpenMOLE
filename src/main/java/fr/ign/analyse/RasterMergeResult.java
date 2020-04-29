package fr.ign.analyse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.geotools.geometry.DirectPosition2D;

/**
 * format of a merged MUP-City output contains few informations about the list of merged raster : - the number of repetitions of the cells are stored into a
 * HashMap<DirectPosition2D, Integer> cellRepet - the mean of the evaluations of the cells are stored into a HashMap<DirectPosition2D, Float> cellEval - a descriptiveStatistic
 * object which im not sure is usefull - a histogram which is surely more useful, containing the number of
 * 
 * @author mcolomb
 *
 */
public class RasterMergeResult {
	HashMap<DirectPosition2D, Integer> cellRepet;
	HashMap<DirectPosition2D, Float> cellEval;
	HashMap<DirectPosition2D, ArrayList<Float>> cellEvals;
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

		HashMap<DirectPosition2D, Float> cellEvalFinal = new HashMap<DirectPosition2D, Float>();
		HashMap<DirectPosition2D, ArrayList<Float>> cellEvalsFinal = new HashMap<DirectPosition2D, ArrayList<Float>>();

		for (RasterMergeResult single : list) {
			HashMap<DirectPosition2D, ArrayList<Float>> cellevalsSingle = single.getCellEvals();
			for (DirectPosition2D dirpos : cellevalsSingle.keySet()) {
				ArrayList<Float> tmp = cellevalsSingle.get(dirpos);
				if (!cellEvalsFinal.isEmpty() && cellEvalFinal.containsKey(dirpos)) {
					tmp.addAll(cellEvalsFinal.get(dirpos));
				}
				cellEvalsFinal.put(dirpos, tmp);
			}
		}
		return finale;

	}

	public HashMap<DirectPosition2D, Integer> getCellRepet() {
		return cellRepet;
	}

	public void setCellRepet(HashMap<DirectPosition2D, Integer> cellRepet) {
		this.cellRepet = cellRepet;
	}

	public HashMap<DirectPosition2D, Float> getCellEval() {
		return cellEval;
	}

	public void setCellEval(HashMap<DirectPosition2D, Float> cellEval) {
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

	public HashMap<DirectPosition2D, ArrayList<Float>> getCellEvals() {
		return this.cellEvals;
	}

	public void setCellEvals(HashMap<DirectPosition2D, ArrayList<Float>> cellevals) {
		this.cellEvals = cellevals;
	}

}
