package fr.ign.exp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;

import fr.ign.analyse.RasterAnalyse;

public class EvaluateObjectiveOfConstructionWithCells {

	/**
	 * 
	 * @param mupOutputFile
	 * @param dataFolder
	 * @throws IOException
	 */
	public static double EvaluateObjectiveConstructionWithCells(File mupOutputFile, File dataFolder)
			throws IOException {

		return EvaluateObjectiveConstructionWithCells(mupOutputFile, dataFolder, "");
	}

	public static double EvaluateObjectiveConstructionWithCellsExcludeBesac(File mupOutputFile, File dataFolder)
			throws IOException {

		return EvaluateObjectiveConstructionWithCells(mupOutputFile, dataFolder, "25056");
	}
	
	
	public static File searchForMUPOutput(File mupOutputFolder) {
		for (File f : mupOutputFolder.listFiles()) {
			if (f.getName().endsWith("evalAnal-20.0.tif")) {
				return f;
			}
		}
		return null;
	}

	public static int EvaluateObjectiveConstructionWithCells(File mupOutputFolder, File dataFolder, String zipToAvoid)
			throws IOException {

		// get the usual mup output
		File mupOutputFile = searchForMUPOutput(mupOutputFolder);

		ShapefileDataStore sds = new ShapefileDataStore((new File(dataFolder, "communities.shp")).toURI().toURL());
		SimpleFeatureIterator comIt = sds.getFeatureSource().getFeatures().features();
		int weight = 0;
		try {
			while (comIt.hasNext()) {
				SimpleFeature feat = comIt.next();
				int objectif = (int) feat.getAttribute("objLgt");
				String insee = (String) feat.getAttribute("DEPCOM");
				// if we want to avoid a city, we continue (for example, if the city is too big
				// and crash the stats
				if (insee.equals(zipToAvoid)) {
					continue;
				}
				// get the number of simulated cells in that city
				int nbCell = RasterAnalyse.getCellsInCity(mupOutputFile, feat);
				weight = weight + Math.abs(objectif - nbCell);
			}
		} catch (Exception problem) {
			problem.printStackTrace();
		} finally {
			comIt.close();
		}
		return weight;
	}

}
