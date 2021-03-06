package fr.ign.analyse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.OverviewPolicy;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterValue;
import org.thema.data.IOImage;

import fr.ign.analyse.obj.Analyse;
import fr.ign.analyse.obj.ScenarAnalyse;

public class RasterMerge {

	public static File middleGridRaster;

	public static void main(String[] args) throws Exception {

		// File fileIn = new File("/media/mcolomb/Data_2/resultExplo/testNov/exOct");
		// File fileOut = new
		// File("/media/mcolomb/Data_2/resultExplo/testNov/exOct/raster/remerged.tif");
		File file1 = new File(
				"/media/mcolomb/Data_2/resultExplo/testNov/exOct/N6_Ba_Yag_ahpx_seed_42/N6_Ba_Yag_ahpx_seed_42-eval_anal-20.0.tif");
		File file2 = new File(
				"/home/mcolomb/workspace/mupcity-openMole/result/exOct/N6_Ba_Yag_ahpx_seed_42/N6_Ba_Yag_ahpx_seed_42-eval_anal-20.0.tif");
		File fileOut = new File(
				"/home/mcolomb/workspace/mupcity-openMole/result/exOct/N6_Ba_Yag_ahpx_seed_42/merged.tiff");
		List<File> lFile = new ArrayList<File>();
		lFile.add(file2);
		lFile.add(file1);
		int echelle = 20;
		merge(lFile, fileOut, echelle);
	}

	/**
	 * merge multiple raster to get their replication. Overloaded in the case of a
	 * raw mupcity output; /!\ Rasters must be the same size /!\
	 * 
	 * @param folderIn  : folder containing files containing raster to merge
	 * @param fileOut   : raster out
	 * @param nameSimul : the code-name of your to-merge rasters
	 * @param filter    : filter the scale that you want to keep
	 * @return the raster file
	 * @throws Exception
	 */
	public static File merge(File folderIn, File fileOut, String nameSimul, final String filter) throws Exception {

		List<File> select = new ArrayList<>();

		for (File f : folderIn.listFiles()) {
			if (f.isDirectory()) {
				for (File ff : f.listFiles()) {
					if (ff.getName().contains(nameSimul) && ff.getName().endsWith("eval_anal-20.0.tif")) {
						select.add(ff);
					}
				}
			}
		}

		int echelle = Integer.parseInt(filter);
		return merge(select, fileOut, echelle);

	}

	/**
	 * overload if the enveloppe needs a crop (in case of a changing grid)
	 * 
	 * @param folderIn
	 * @param fileOut
	 * @param echelle
	 * @return
	 * @throws Exception
	 */
	public static File merge(List<ScenarAnalyse> ScenarsIn, Analyse anal, File fileOut, boolean crop) throws Exception {
		List<File> inList = new ArrayList<File>();
		for (ScenarAnalyse sA : ScenarsIn) {
			inList.add(anal.getSimuFile(sA));
		}

		return merge(inList, fileOut, Integer.valueOf(ScenarsIn.get(0).getSizeCell()), crop);
	}

	public static File merge(List<File> folderIn, File fileOut, int ech) throws Exception {
		return merge(folderIn, fileOut, ech, false);
	}

	/**
	 * 
	 * @param folderIn : list of raster file to merge
	 * @param fileOut  : file where the raster is merged
	 * @param ech      : size of the last decomposed cell
	 * @param crop     : if true, the envelope is croped by the lenght of a cell
	 *                 size
	 * @return the merged file
	 * @throws Exception
	 */
	public static File merge(List<File> folderIn, File fileOut, int ech, boolean crop) throws Exception {

		// just to make sure
		if (!fileOut.getName().endsWith(".tif")) {
			fileOut = new File(fileOut + ".tif");
			System.out.println("stupeeds");
		}

		// setting of useless parameters
		ParameterValue<OverviewPolicy> policy = AbstractGridFormat.OVERVIEW_POLICY.createValue();
		policy.setValue(OverviewPolicy.IGNORE);
		// this will basically read 4 tiles worth of data at once from the
		// disk...
		ParameterValue<String> gridsize = AbstractGridFormat.SUGGESTED_TILE_SIZE.createValue();
		// Setting read type: use JAI ImageRead (true) or ImageReaders read
		// methods (false)
		ParameterValue<Boolean> useJaiRead = AbstractGridFormat.USE_JAI_IMAGEREAD.createValue();
		useJaiRead.setValue(false);
		GeneralParameterValue[] params = new GeneralParameterValue[] { policy, gridsize, useJaiRead };

		// set matrice

		GeoTiffReader readerSet = new GeoTiffReader(folderIn.get(0));
		GridCoverage2D coverageSet = readerSet.read(params);
		Envelope2D env = coverageSet.getEnvelope2D();
		// if we crop the enveloppe, we need to take as a reference the middle enveloppe
		if (crop) {
			readerSet = new GeoTiffReader(middleGridRaster);
			System.out.println(middleGridRaster);
			coverageSet = readerSet.read(params);
			env = coverageSet.getEnvelope2D();
			System.out.println("second env " + env);
		}

		float[][] imagePixelData = new float[(int) Math.floor(env.getWidth() / ech)][(int) Math
				.floor(env.getHeight() / ech)];

		double xMin = env.getMinX();
		double yMin = env.getMinY();

		int longueur = imagePixelData.length;
		int largeur = imagePixelData[0].length;

		// if a crop on the area is needed
		if (crop) {
			xMin = xMin + ech;
			longueur = longueur - ech;
			yMin = yMin + ech;
			largeur = largeur - ech;
		}

		for (int fInd = 0; fInd < (folderIn.size()); fInd++) {
			GeoTiffReader reader = new GeoTiffReader(folderIn.get(fInd));
			GridCoverage2D coverage = reader.read(params);
			for (int i = 0; i < longueur; ++i) {
				for (int j = 0; j < largeur; ++j) {
					DirectPosition2D pt = new DirectPosition2D(xMin + (2 * i + 1) * ech / 2,
							yMin + (2 * j + 1) * ech / 2);
					float[] val = (float[]) coverage.evaluate(pt);
					if (val[0] > 0) {
						imagePixelData[i][j] = imagePixelData[i][j] + 1;
					}
				}
			}
		}

		// could be simpler.. but translation because the x and y are not stored equally
		// in the two objects
		float[][] imgpix2 = new float[imagePixelData[0].length][imagePixelData.length];
		float[][] imgpix3 = new float[imagePixelData[0].length][imagePixelData.length];
		for (int i = 0; i < imgpix2.length; ++i) {
			for (int j = 0; j < imgpix2[0].length; ++j) {
				imgpix2[i][j] = imagePixelData[imgpix2[0].length - 1 - j][i];
			}
		}
		for (int i = 0; i < imgpix3.length; ++i) {
			for (int j = 0; j < imgpix3[0].length; ++j) {
				imgpix3[i][j] = imgpix2[imgpix3.length - 1 - i][imgpix3[0].length - 1 - j];
			}
		}
		writeGeotiff(fileOut, imgpix3, env);
		return fileOut;
	}

	public static void writeGeotiff(HashMap<DirectPosition2D, Integer> table, File fileOut, int ech,
			File exempleRaster) throws IOException {
		HashMap<DirectPosition2D, Float> convert = new HashMap<DirectPosition2D, Float>();

		for (DirectPosition2D pos : table.keySet()) {
			convert.put(pos, (float) table.get(pos));
		}
		writeGeotiff(convert, ech, fileOut, exempleRaster);

	}

	public static void writeGeotiff(HashMap<DirectPosition2D, Float> table, int ech, File fileOut, File exempleRaster)
			throws IOException {
		ParameterValue<OverviewPolicy> policy = AbstractGridFormat.OVERVIEW_POLICY.createValue();
		policy.setValue(OverviewPolicy.IGNORE);
		// this will basically read 4 tiles worth of data at once from the
		// disk...
		ParameterValue<String> gridsize = AbstractGridFormat.SUGGESTED_TILE_SIZE.createValue();
		// Setting read type: use JAI ImageRead (true) or ImageReaders read
		// methods (false)
		ParameterValue<Boolean> useJaiRead = AbstractGridFormat.USE_JAI_IMAGEREAD.createValue();
		useJaiRead.setValue(false);
		GeneralParameterValue[] params = new GeneralParameterValue[] { policy, gridsize, useJaiRead };

		// set matrice

		GeoTiffReader readerSet = new GeoTiffReader(exempleRaster);
		GridCoverage2D coverageSet = readerSet.read(params);
		Envelope2D env = coverageSet.getEnvelope2D();

		float[][] imagePixelData = new float[(int) Math.floor(env.getWidth() / ech)][(int) Math
				.floor(env.getHeight() / ech)];

		double xMin = env.getMinX();
		double yMin = env.getMinY();

		int longueur = imagePixelData.length;
		int largeur = imagePixelData[0].length;

		for (int i = 0; i < longueur; ++i) {
			for (int j = 0; j < largeur; ++j) {
				DirectPosition2D pt = new DirectPosition2D(xMin + (2 * i + 1) * ech / 2, yMin + (2 * j + 1) * ech / 2);
				try {
					imagePixelData[i][j] = table.get(pt);
				} catch (NullPointerException n) {

				}
			}
		}
		float[][] imgpix2 = new float[imagePixelData[0].length][imagePixelData.length];
		float[][] imgpix3 = new float[imagePixelData[0].length][imagePixelData.length];
		for (int i = 0; i < imgpix2.length; ++i) {
			for (int j = 0; j < imgpix2[0].length; ++j) {
				imgpix2[i][j] = imagePixelData[imgpix2[0].length - 1 - j][i];
			}
		}
		for (int i = 0; i < imgpix3.length; ++i) {
			for (int j = 0; j < imgpix3[0].length; ++j) {
				imgpix3[i][j] = imgpix2[imgpix3.length - 1 - i][imgpix3[0].length - 1 - j];
			}
		}

		writeGeotiff(fileOut, imgpix3, env);
	}

	/**
	 * export raster
	 * @see i don't know why it's done with thema libs (another method is available within the Artiscale-tools.Raster class)
	 * @param fileName
	 * @param imagePixelData
	 * @param env
	 */
	public static void writeGeotiff(File fileName, float[][] imagePixelData, Envelope2D env) {
		GridCoverage2D coverage = new GridCoverageFactory().create("OTPAnalyst", imagePixelData, env);
		writeGeotiff(fileName, coverage);
	}

	public static void writeGeotiff(File fileName, GridCoverage2D coverage) {
		try {
			IOImage.saveTiffCoverage(fileName, coverage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}