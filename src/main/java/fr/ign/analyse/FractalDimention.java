package fr.ign.analyse;

import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.jts.geom.Geometry;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.thema.data.IOImage;
import org.thema.data.feature.DefaultFeature;
import org.thema.data.feature.DefaultFeatureCoverage;
import org.thema.data.feature.Feature;
import org.thema.process.Rasterizer;

import fr.ign.artiscales.tools.geoToolsFunctions.Csv;
import fr.ign.artiscales.tools.geoToolsFunctions.Rasters;
import fr.ign.tools.OutputTools;


public class FractalDimention {
	public static void main(String[] args) throws Exception {
		// int resolution = 5;
		// File fileOut = new File("/home/mcolomb/tmp/fractDimS.tif");
		// fileOut.mkdirs();
		// File batiFile = new
		// File("/media/mcolomb/Data_2/dataOpenMole/stabilite/dataManu/batimentPro.shp");
		// // File rootFile = new
		// File("/media/mcolomb/Data_2/resultExplo/Stability/N5MoySt");
		// // getCorrFracDimfromSimu(batiFile, rootFile, fileOut, resolution);
		// File testFile = new File("");
		// getCorrFracDim(batiFile, testFile, fileOut, resolution,
		// "seed_8600511651180259677");
		// fileOut = new File("/home/mcolomb/tmp/fractDimT.tif");
		// testFile = new File(
		// "/media/mcolomb/Data_2/resultFinal/testAHP/Stabilite-testAHP-Autom-CM20.0-S0.0-GP_915948.0_6677337.0/N5_Ba_MoyahpT_Moy_seed_42/N5_Ba_MoyahpT_Moy_seed_42-evalAnal-20.0.tif");
		// getCorrFracDim(batiFile, testFile, fileOut, resolution,
		// "seed_8600511651180259677");
		File base =	new File("/home/yo/Documents/these/lastDimFract/out");
		List<File> toStudy = new ArrayList<File>();
		toStudy.add(new File(base, "Stability-dataManu-CM20.0-S0.0-GP_915948.0_6677337.0--N4_St_Moy_ahpx_seed_5621105064239176722-evalAnal-20.0.tif"));
		toStudy.add(new File(base, "N4_St_Moy_ahpS_seed_42-evalAnal-20.0.tif"));
//		toStudy.add(new File(base, "N4_St_MoyahpT_Moy_seed_15711"));

		toStudy.add(new File(base, "Stability-dataManu-CM20.0-S0.0-GP_915948.0_6677337.0--N5_Ba_Moy_ahpx_seed_6274526350136348928-evalAnal-20.0.tif"));
		toStudy.add(new File(base, "N5_Ba_Moy_ahpS_seed_42-evalAnal-20.0.tif"));
//		toStudy.add(new File(base, "N5_Ba_MoyahpT_Moy_seed_42"));

		toStudy.add(new File(base, "Stability-dataManu-CM20.0-S0.0-GP_915948.0_6677337.0--N6_St_Moy_ahpx_seed_6395953612790931483-evalAnal-20.0.tif"));
		toStudy.add(new File(base, "N6_St_Moy_ahpS_seed_42-evalAnal-20.0.tif"));
//		toStudy.add(new File(base, "N6_St_MoyahpT_Moy_seed_16683"));

		toStudy.add(new File(base, "Stability-dataManu-CM20.0-S0.0-GP_915948.0_6677337.0--N7_Ba_Yag_ahpx_seed_7452225133228106350-evalAnal-20.0.tif"));
		toStudy.add(new File(base, "N7_Ba_Yag_ahpS_seed_42-evalAnal-20.0.tif"));
//		toStudy.add(new File(base, "N7_Ba_YagahpT_Yag_seed_26483"));
		
		File outFile = new File("/home/yo/Documents/these/lastDimFract");

//		toStudy.add(new File(
//				"/home/yo/Documents/these/resultFinal/dataManu/dataManu-CM20.0-S0.0-GP_915948.0_6677337.0--N4_St_Moy_ahpx/SortieExemple/Stability-dataManu-CM20.0-S0.0-GP_915948.0_6677337.0--N4_St_Moy_ahpx_seed_5621105064239176722-eval-20.0.tif"));
//
//		File empty = new File("/home/yo/Documents/these/resultFinal/emptyOutputreal.tif");
//		getCorrFracDim(new File("/home/yo/Documents/these/data/stabilite/dataManu/batimentPro.shp"), empty, outFile, 4,
//				"situ init");

		for (File f : toStudy) {
			getCorrFracDim(new File("/home/yo/Documents/these/data/stabilite/dataManu/batimentPro.shp"), f, outFile, 4,
					f.getName());
		}
	}

	public static HashMap<String, HashMap<String, Double>> getCorrFracDim(File batiFile, File fileOut,
			int resolution, String name) throws IOException {
		HashMap<String, HashMap<String, Double>> results = new HashMap<String, HashMap<String, Double>>();
		results.put(name,
				calculFracCor(
						importRaster(
								rasterize(batiFile, new File(fileOut.getParentFile(), "temprast.tif"), resolution)),
						fileOut));
		Csv.generateCsvFileMultTab(results, fileOut, "dimensionFractale");
		File dF = new File(fileOut.getParentFile(), "temprast.tif");
		dF.delete();
		return results;
	}

	/**
	 * get the fractal dimension (calculated with the correlation method) of a
	 * MUP-City output by mergini it with a build file
	 * 
	 * @param batiFile   : build file
	 * @param mupFile    : MUP-City output
	 * @param fileOut    : .csv file where the calculations are written
	 * @param resolution : resolution of the rasterization of the builing/MUPoutput
	 *                   merge
	 * @param name       : same of the MUP-City scenario
	 * @return an HashMap containing the result of the calculation
	 * @throws IOException
	 */
	public static HashMap<String, HashMap<String, Double>> getCorrFracDim(File batiFile, File mupFile, File fileOut,
			int resolution, String name) throws IOException {
		HashMap<String, HashMap<String, Double>> results = new HashMap<String, HashMap<String, Double>>();
		if (mupFile.isDirectory()) {
			mupFile = OutputTools.getMupFileFromFolder(mupFile, String.valueOf(resolution));
		}
		results.put(name, calculFracCor(mergeBuildMUPResultRast(batiFile, mupFile, fileOut, resolution), fileOut));

		Csv.generateCsvFileMultTab(results, fileOut, "dimensionFractale");
		File dF = new File(fileOut.getParentFile(), "temprast.tif");
		dF.delete();
		return results;
	}

	/**
	 * Dedicaded method to merge a building file with a MUP-City output
	 * 
	 * @param batiFile
	 * @param MUPFile
	 * @param fileOut
	 * @param resolution
	 * @return a raster layer from the emprise of the mup-city's output
	 * @throws IOException
	 */
	public static GridCoverage2D mergeBuildMUPResultRast(File batiFile, File MUPFile, File fileOut, int resolution)
			throws IOException {
		GridCoverage2D coverage = importRaster(MUPFile);
		File rasterBatiFile = rasterize(batiFile,
				new File(fileOut.getParentFile(), "batiRasterized" + resolution+fileOut.getName() + ".tif"), resolution);
		GridCoverage2D rasterBati = importRaster(rasterBatiFile);

		CoordinateReferenceSystem sourceCRS = coverage.getCoordinateReferenceSystem();

		ReferencedEnvelope mupBounds = new ReferencedEnvelope(coverage.getEnvelope2D(), sourceCRS);
		ReferencedEnvelope batiBounds = new ReferencedEnvelope(rasterBati.getEnvelope2D(), sourceCRS);
		ReferencedEnvelope gridBounds = mupBounds.intersection(batiBounds);

		float[][] imagePixelData = new float[(int) Math.floor(gridBounds.getWidth() / resolution)][(int) Math
				.floor(gridBounds.getHeight() / resolution)];
		double Xmin = gridBounds.getMinX();
		double Ymin = gridBounds.getMinY();

		for (int i = 0; i < imagePixelData.length; ++i) {
			for (int j = 0; j < imagePixelData[0].length; ++j) {
				DirectPosition2D pt = new DirectPosition2D(Xmin + (2 * i + 1) * resolution / 2,
						Ymin + (2 * j + 1) * resolution / 2);
				float[] val = (float[]) coverage.evaluate(pt);
				byte[] bat = (byte[]) rasterBati.evaluate(pt);

				if (val[0] > 0 || bat[0] > 0) {
					imagePixelData[i][j] = 1;
				} else {
					imagePixelData[i][j] = 0;
				}
			}
		}

		// transfo to put into a new rasterFile (yeah, too complicated)
		float[][] imgpix2 = new float[imagePixelData[0].length][imagePixelData.length];
		float[][] imgpix3 = new float[imagePixelData[0].length][imagePixelData.length];

		// System.out.println("imgpix2");
		for (int i = 0; i < imgpix2.length; ++i) {
			for (int j = 0; j < imgpix2[0].length; ++j) {
				imgpix2[i][j] = imagePixelData[imgpix2[0].length - 1 - j][i];
			}
		}

		// System.out.println("imgpix3");
		for (int i = 0; i < imgpix3.length; ++i) {
			for (int j = 0; j < imgpix3[0].length; ++j) {
				imgpix3[i][j] = imgpix2[imgpix3.length - 1 - i][imgpix3[0].length - 1 - j];
			}
		}

		// System.out.println("toTestRaster");
		GridCoverage2D toTestRaster = new GridCoverageFactory().create("bati", imgpix3, gridBounds);
		Rasters.writeGeotiff(new File("/tmp/salut.tif"), toTestRaster);
		// RasterMerge.writeGeotiff(new File("/home/mcolomb/rastermergedtmp.tif"),
		// toTestRaster);
		return toTestRaster;
	}

	public static HashMap<String, Double> calculFracCor(GridCoverage2D toTestRaster, File fileOut)
			throws IOException {
//		Rasters.writeGeotiff(new File(fileOut, "tmp"), toTestRaster);
//		DefaultSampling dS = new DefaultSampling(22, 3000, 1.5, Sequence.GEOM);
//		CorrelationRasterMethod correlation = new CorrelationRasterMethod("test", dS, toTestRaster.getRenderedImage(),
//				JTS.rectToEnv(toTestRaster.getEnvelope2D()));
//
//		correlation.execute(new TaskMonitor.EmptyMonitor(), true);
//
//		Estimation estim = new EstimationFactory(correlation).getDefaultEstimation();
//
//		HashMap<String, Double> values = new HashMap<String, Double>();
//		values.put("dimension de corrélation", estim.getDimension());
//		values.put("R2", estim.getR2());
//		values.put("BootStrap Confidence Interval Low", estim.getBootStrapConfidenceInterval()[0]);
//		values.put("BootStrap Confidence Interval High", estim.getBootStrapConfidenceInterval()[1]);
//		System.out.println("dimension de corrélation " + values.get("dimension de corrélation"));
//		System.out.println("R2 " + values.get("R2"));
//		System.out.println("BootStrap Confidence Interval Low : " + values.get("BootStrap Confidence Interval Low"));
//		System.out.println("BootStrap Confidence Interval High : " + values.get("BootStrap Confidence Interval High"));
//		return values;
		System.out.println("fractal calculation desactivated");
		return null;
	}

	public static File rasterize(File batiFile, File fileOut, int resolution)
			throws MalformedURLException, IOException {
//		if (!fileOut.exists()) {
//		HashSet<Feature> batiCol = new HashSet<>();
//
//		ShapefileDataStore batiDS = new ShapefileDataStore((batiFile).toURI().toURL());
//		SimpleFeatureCollection bati = batiDS.getFeatureSource().getFeatures();
//
//		CoordinateReferenceSystem sourceCRS = bati.getSchema().getCoordinateReferenceSystem();
//		// rasterisation avec les outils de théma
//		// create a thema collection
//		int h = 0;
//		SimpleFeatureIterator iteratorBati = bati.features();
//		try {
//			// Pour toutes les entitées
//			while (iteratorBati.hasNext()) {
//				Feature f = new DefaultFeature((Object) h, (Geometry) (iteratorBati.next()).getDefaultGeometry());
//				h = h + 1;
//				batiCol.add(f);
//			}
//		} catch (Exception problem) {
//			problem.printStackTrace();
//		} finally {
//			iteratorBati.close();
//		}
//
//		DefaultFeatureCoverage<Feature> featCov = new DefaultFeatureCoverage<Feature>(batiCol);
//		Rasterizer rast = new Rasterizer(featCov, resolution);
//		WritableRaster wRaster = rast.rasterize(null);
//		ReferencedEnvelope envBati = new ReferencedEnvelope(featCov.getEnvelope().getMinX(),
//				featCov.getEnvelope().getMaxX(), featCov.getEnvelope().getMinY(), featCov.getEnvelope().getMaxY(),
//				sourceCRS);
//		GridCoverage2D rasterBati = new GridCoverageFactory().create("bati", wRaster, envBati);
//		writeGeotiff(fileOut, rasterBati);
//		batiDS.dispose();
//		}
//		else {
//			System.out.println("rasterized build already exists");
//		}
		return fileOut;
	}

	public static void writeGeotiff(File fileName, GridCoverage2D coverage) {
		try {
			// GeoTiffWriteParams wp = new GeoTiffWriteParams();
			// wp.setCompressionMode(GeoTiffWriteParams.MODE_EXPLICIT);
			// wp.setCompressionType("LZW");
			// ParameterValueGroup params = new GeoTiffFormat().getWriteParameters();
			// params.parameter(AbstractGridFormat.GEOTOOLS_WRITE_PARAMS.getName().toString()).setValue(wp);
			// GeoTiffWriter writer = new GeoTiffWriter(fileName);
			// writer.write(coverage, (GeneralParameterValue[]) params.values().toArray(new
			// GeneralParameterValue[1]));
			IOImage.saveTiffCoverage(fileName, coverage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static GridCoverage2D importRaster(File rasterIn) throws IOException {
		// ParameterValue<OverviewPolicy> policy =
		// AbstractGridFormat.OVERVIEW_POLICY.createValue();
		// policy.setValue(OverviewPolicy.IGNORE);
		// ParameterValue<String> gridsize =
		// AbstractGridFormat.SUGGESTED_TILE_SIZE.createValue();
		// ParameterValue<Boolean> useJaiRead =
		// AbstractGridFormat.USE_JAI_IMAGEREAD.createValue();
		// useJaiRead.setValue(true);
		// GeneralParameterValue[] params = new GeneralParameterValue[] { policy,
		// gridsize, useJaiRead };
		// GridCoverage2DReader reader = new GeoTiffReader(rasterIn);
		// GridCoverage2D coverage = reader.read(params);
		// return coverage;
		return IOImage.loadTiff(rasterIn);
	}
}
