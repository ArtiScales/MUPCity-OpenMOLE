package fr.ign.analyse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import org.geotools.coverage.grid.GridCoordinates2D;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.coverage.grid.io.OverviewPolicy;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;
import org.opengis.coverage.grid.GridCoordinates;
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterValue;

public class RasterSynth {

	public static void main(String[] args) throws IOException {
		// génère un raster avec uniquement des cellules stables (a plusieurs echelles différentes)
		
		
		File fileIn = new File("/home/mcolomb/donnee/autom/besancon/donnee-carte/diff-batie.tif");
		
		System.out.println(countCellPos(fileIn ));
	}

	public static int countCellPos(File file) throws IOException {
		int nbCell = 0;
		ParameterValue<OverviewPolicy> policy = AbstractGridFormat.OVERVIEW_POLICY.createValue();
		policy.setValue(OverviewPolicy.IGNORE);
		ParameterValue<String> gridsize = AbstractGridFormat.SUGGESTED_TILE_SIZE.createValue();
		ParameterValue<Boolean> useJaiRead = AbstractGridFormat.USE_JAI_IMAGEREAD.createValue();
		useJaiRead.setValue(true);
		GeneralParameterValue[] params = new GeneralParameterValue[] { policy, gridsize, useJaiRead };
		GridCoverage2DReader reader = new GeoTiffReader(file);
		GridCoverage2D coverage = reader.read(params);
		Envelope2D env = coverage.getEnvelope2D();
		System.out.println("minX : "+env.getMinX()+"maxX : "+env.getMaxX()+"minY : "+env.getMinY()+"maxY : "+env.getMaxY());
		
		for (double i = env.getMinX()+10; i < env.getMaxX(); i = i + 20) {
			for (double j = env.getMinY()+10; j < env.getMaxY(); j = j + 20) {
				DirectPosition2D pt = new DirectPosition2D(i, j);
				float[] val = (float[]) coverage.evaluate(pt);
				if (val[0] !=0) {
					nbCell = nbCell + 1;
				}
			}
		}
		return nbCell;
	}

	public static void selectRasterStable(ArrayList<File> listRepliFile) throws IOException {

		Hashtable<GridCoordinates2D, Integer> cellRepet = new Hashtable<GridCoordinates2D, Integer>();

		int nbDeScenar = 0;

		float[][] imagePixelData = null;
		float[][] imagePD = null;
		Envelope2D env = null;

		for (File f : listRepliFile) {
			// setting of useless parameters
			ParameterValue<OverviewPolicy> policy = AbstractGridFormat.OVERVIEW_POLICY.createValue();
			policy.setValue(OverviewPolicy.IGNORE);
			ParameterValue<String> gridsize = AbstractGridFormat.SUGGESTED_TILE_SIZE.createValue();
			ParameterValue<Boolean> useJaiRead = AbstractGridFormat.USE_JAI_IMAGEREAD.createValue();
			useJaiRead.setValue(false);
			GeneralParameterValue[] params = new GeneralParameterValue[] { policy, gridsize, useJaiRead };

			GridCoverage2DReader reader = new GeoTiffReader(f);
			GridCoverage2D coverage = reader.read(params);
			GridEnvelope dimensions = reader.getOriginalGridRange();
			GridCoordinates maxDimensions = dimensions.getHigh();

			int w = maxDimensions.getCoordinateValue(0) + 1;
			int h = maxDimensions.getCoordinateValue(1) + 1;
			int numBands = reader.getGridCoverageCount();
			double[] vals = new double[numBands];
			if (env == null) {
				env = coverage.getEnvelope2D();
			}
			int compteurNombre = 0;
			nbDeScenar = nbDeScenar + 1;

			// beginning of the all cells loop
			int debI = 0;
			int debJ = 0;

			// in case of a move of the grid, we may have to delete the border cells because they will be moved

			for (int i = debI; i < w; i++) {
				for (int j = debJ; j < h; j++) {
					if (imagePixelData == null) {
						imagePixelData = new float[w][h];
						imagePixelData[i][j] = 0;
					}
					if (imagePD == null) {
						imagePD = new float[w][h];
						imagePD[i][j] = 0;
					}
					GridCoordinates2D coord = new GridCoordinates2D(i, j);

					if (coverage.evaluate(coord, vals)[0] > 0) {
						compteurNombre = compteurNombre + 1;
						if (cellRepet.containsKey(coord)) { // si la cellule a déja été sélectionné lors de réplications
							cellRepet.put(coord, cellRepet.get(coord) + 1);
							imagePD[i][j]++;
						} else {// si la cellule est sélectionné pour la première fois
							cellRepet.put(coord, 1);
							ArrayList<Float> firstList = new ArrayList<Float>();
							firstList.add((float) coverage.evaluate(coord, vals)[0]);
							imagePD[i][j]++;
						}
					}
				}
			}
		}
	}
}
