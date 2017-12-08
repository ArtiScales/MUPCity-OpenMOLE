package org.thema.mupcity.task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.opengis.filter.FilterFactory2;
import org.thema.mupcity.analyse.FractalDimention;
import org.thema.mupcity.analyse.RasterAnalyse;
import org.thema.mupcity.analyse.RasterMerge;

import com.google.common.io.Files;

public class RasterAnalyseTask {

	public static String echelle;

	public static void main(String[] args) throws Exception {
		File file = new File("/home/mcolomb/workspace/mupcity-openMole/result/newRoad-serviceSans");
		File discreteFile = new File("/home/mcolomb/informatique/MUP/explo/dataExtra/admin_typo.shp");
		File batiFile = new File("/home/mcolomb/donnee/couplage/donneeGeographiques/batiment.shp");
		runStab(file, discreteFile, batiFile, "stats-dicrete");
	}

	public static File runGridSens(File file, int minCell, File discreteFile, File batiFile, String name) throws Exception {
		RasterAnalyse.rootFile = file;
		int[] listEch = { minCell, minCell * 3, minCell * 9 };
		for (int ech : listEch) {

			RasterAnalyse.echelle = String.valueOf(ech);
			RasterAnalyse.cutBorder = true;
			ArrayList<File> listRepliFile = new ArrayList<File>();
			for (File f : file.listFiles()) {
				for (File ff : f.listFiles()) {
					if (ff.toString().endsWith("eval_anal-" + echelle + ".0.tif")) {
						listRepliFile.add(ff);
					}
				}
			}
			RasterAnalyse.mergeRasters(listRepliFile, "gridSensibility");
			RasterAnalyse.discrete = true;
			int count = 0 ;
			 for (File f : listRepliFile) {
				 count=count+1;
			 
			 ArrayList<File> singleCity = new ArrayList<File>();
			 singleCity.add(f);
			 RasterAnalyse.mergeRasters(singleCity, "cityGen" + count);
			 listRepliFile = new ArrayList<File>();
			 }

			RasterAnalyse.gridChange();
		}
		return batiFile;
	}
	
	public static File runStab(File file, File discreteFile, File batiFile, String name) throws Exception {
		File resultFile = new File(file, "result");
		for (File f : file.listFiles()) {
			if (f.getName().startsWith("N")) {
				copyDirectory(f, new File(resultFile, "SortieExemple"));
				break;
			}
		}

		File statFile = null;
		File rastFile = new File(file, "raster");
		rastFile.mkdir();

		for (int ech = 20; ech <= 180; ech = ech * 3) {
			echelle = String.valueOf(ech);
			RasterAnalyse.rootFile = file;
			RasterAnalyse.discrete = false;
			RasterAnalyse.echelle = echelle;
			RasterAnalyse.stabilite = true;

			List<File> fileToTest = new ArrayList<File>();

			for (File f : file.listFiles()) {
				if (f.isDirectory()) {
					for (File ff : f.listFiles()) {
						if (ff.getName().endsWith("eval_anal-" + echelle + ".0.tif")) {
							fileToTest.add(ff);
						}
					}
				}
			}

			statFile = RasterAnalyse.mergeRasters(fileToTest, name);

			System.out.println(statFile);
			// discrete analysis
			RasterAnalyse.discrete = true;
			RasterAnalyse.discreteFile = discreteFile;

			RasterAnalyse.mergeRasters(fileToTest, "stat-discrete");

			RasterMerge.merge(fileToTest, new File(rastFile, "rasterMerged.tif"), 20);

			int resolution = 4;
			FractalDimention.getCorrFracDimfromSimu(batiFile, file, statFile, echelle, resolution);

		}
		copyDirectory(statFile, new File(resultFile, "Stat"));
		copyDirectory(rastFile, new File(resultFile, "Raster"));
		return statFile;
	}

	public static void copyDirectory(File copDir, File destinationDir) throws IOException {
		destinationDir.mkdirs();
		for (File f : copDir.listFiles()) {
			Files.copy(f, new File(destinationDir, f.getName()));
		}
	}
}
