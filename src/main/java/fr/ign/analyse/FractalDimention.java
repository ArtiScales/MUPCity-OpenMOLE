package fr.ign.analyse;

import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Hashtable;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.coverage.grid.io.OverviewPolicy;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterValue;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.thema.common.JTS;
import org.thema.common.swing.TaskMonitor;
import org.thema.data.IOImage;
import org.thema.data.feature.DefaultFeature;
import org.thema.data.feature.DefaultFeatureCoverage;
import org.thema.data.feature.Feature;
import org.thema.fracgis.estimation.Estimation;
import org.thema.fracgis.estimation.EstimationFactory;
import org.thema.fracgis.method.raster.mono.CorrelationRasterMethod;
import org.thema.fracgis.sampling.DefaultSampling;
import org.thema.fracgis.sampling.Sampling.Sequence;
import org.thema.process.Rasterizer;

import com.vividsolutions.jts.geom.Geometry;

import fr.ign.cogit.GTFunctions.Csv;

public class FractalDimention {
	public static void main(String[] args) throws Exception {
		int resolution = 5;
		File fileOut = new File("/home/yo/tmp/try.tif");
		fileOut.mkdirs();
		File batiFile = new File("/home/yo/Documents/these/data/stabilite/dataManu/batimentPro.shp");
		// File rootFile = new File("/media/mcolomb/Data_2/resultExplo/Stability/N5MoySt");
		// getCorrFracDimfromSimu(batiFile, rootFile, fileOut, resolution);
		File testFile = new File(
				"/home/yo/Documents/these/resultFinal/dataAutom/dataAutom-CM20.0-S0.0-GP_915948.0_6677337.0--N5_Ba_Yag_ahpx/SortieExemple/Stability-dataAutom-CM20.0-S0.0-GP_915948.0_6677337.0--N5_Ba_Yag_ahpx_seed_6263036718188638317-evalAnal-20.0.tif");
		getCorrFracDim(batiFile, testFile, fileOut, resolution, "seed_8600511651180259677");
	}

	/**
	 * get the fractal dimension (calculated with the correlation method) of a MUP-City output by mergini it with a build file
	 * 
	 * @param batiFile
	 *            : build file
	 * @param mupFile
	 *            : MUP-City output
	 * @param fileOut
	 *            : .csv file where the calculations are written
	 * @param resolution
	 *            : resolution of the rasterization of the builing/MUPoutput merge
	 * @param name
	 *            : same of the MUP-City scenario
	 * @return an Hashtable containing the result of the calculation
	 * @throws IOException
	 */
	public static Hashtable<String, Hashtable<String, Double>> getCorrFracDim(File batiFile, File mupFile, File fileOut, int resolution, String name) throws IOException {
		Hashtable<String, Hashtable<String, Double>> results = new Hashtable<String, Hashtable<String, Double>>();
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
	public static GridCoverage2D mergeBuildMUPResultRast(File batiFile, File MUPFile, File fileOut, int resolution) throws IOException {
		GridCoverage2D coverage = importRaster(MUPFile);
		File rasterBatiFile = rasterize(batiFile, new File(fileOut.getParentFile(), "temprast.tif"));
		GridCoverage2D rasterBati = importRaster(rasterBatiFile);

		CoordinateReferenceSystem sourceCRS = coverage.getCoordinateReferenceSystem();

		ReferencedEnvelope mupBounds = new ReferencedEnvelope(coverage.getEnvelope2D(), sourceCRS);
		ReferencedEnvelope batiBounds = new ReferencedEnvelope(rasterBati.getEnvelope2D(), sourceCRS);
		ReferencedEnvelope gridBounds = mupBounds.intersection(batiBounds);

		float[][] imagePixelData = new float[(int) Math.floor(gridBounds.getWidth() / resolution)][(int) Math.floor(gridBounds.getHeight() / resolution)];
		double Xmin = gridBounds.getMinX();
		double Ymin = gridBounds.getMinY();

		for (int i = 0; i < imagePixelData.length; ++i) {
			for (int j = 0; j < imagePixelData[0].length; ++j) {
				DirectPosition2D pt = new DirectPosition2D(Xmin + (2 * i + 1) * resolution / 2, Ymin + (2 * j + 1) * resolution / 2);
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

		RasterMerge.writeGeotiff(new File("/home/yo/tmp.tif"), toTestRaster);
		return toTestRaster;
	}

	public static Hashtable<String, Double> calculFracCor(GridCoverage2D toTestRaster, File fileOut) throws IOException {
		// System.out.println("\tstart calculFracCor");
		DefaultSampling dS = new DefaultSampling(22, 3000, 1.5, Sequence.GEOM);
		// System.out.println("\tcorrelation");
		CorrelationRasterMethod correlation = new CorrelationRasterMethod("test", dS, toTestRaster.getRenderedImage(), JTS.rectToEnv(toTestRaster.getEnvelope2D()));

		// System.out.println("\tcorrelation execute");
		correlation.execute(new TaskMonitor.EmptyMonitor(), true);

		// System.out.println("\testimation");
		Estimation estim = new EstimationFactory(correlation).getDefaultEstimation();

		Hashtable<String, Double> values = new Hashtable<String, Double>();
		values.put("dimension de corrélation", estim.getDimension());
		values.put("R2", estim.getR2());
		values.put("BootStrap Confidence Interval Low", estim.getBootStrapConfidenceInterval()[0]);
		values.put("BootStrap Confidence Interval High", estim.getBootStrapConfidenceInterval()[1]);
		System.out.println("dimension de corrélation " + values.get("dimension de corrélation"));
		System.out.println("R2 " + values.get("R2"));
		System.out.println("BootStrap Confidence Interval Low : " + values.get("BootStrap Confidence Interval Low"));
		System.out.println("BootStrap Confidence Interval High : " + values.get("BootStrap Confidence Interval High"));
		return values;
	}

	public static File rasterize(File batiFile, File fileOut) throws MalformedURLException, IOException {
		HashSet<Feature> batiCol = new HashSet<>();
		if (!fileOut.exists()) {
			ShapefileDataStore batiDS = new ShapefileDataStore((batiFile).toURI().toURL());
			SimpleFeatureCollection bati = batiDS.getFeatureSource().getFeatures();

			CoordinateReferenceSystem sourceCRS = bati.getSchema().getCoordinateReferenceSystem();
			// rasterisation avec les outils de théma
			// create a thema collection
			int h = 0;
			SimpleFeatureIterator iteratorBati = bati.features();
			try {
				// Pour toutes les entitées
				while (iteratorBati.hasNext()) {
					Feature f = new DefaultFeature((Object) h, (Geometry) (iteratorBati.next()).getDefaultGeometry());
					h = h + 1;
					batiCol.add(f);
				}
			} catch (Exception problem) {
				problem.printStackTrace();
			} finally {
				iteratorBati.close();
			}

			DefaultFeatureCoverage<Feature> featCov = new DefaultFeatureCoverage<Feature>(batiCol);
			Rasterizer rast = new Rasterizer(featCov, 4);
			WritableRaster wRaster = rast.rasterize(null);
			ReferencedEnvelope envBati = new ReferencedEnvelope(featCov.getEnvelope().getMinX(), featCov.getEnvelope().getMaxX(), featCov.getEnvelope().getMinY(),
					featCov.getEnvelope().getMaxY(), sourceCRS);
			GridCoverage2D rasterBati = new GridCoverageFactory().create("bati", wRaster, envBati);
			writeGeotiff(fileOut, rasterBati);
			batiDS.dispose();
		}
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
			// writer.write(coverage, (GeneralParameterValue[]) params.values().toArray(new GeneralParameterValue[1]));
			IOImage.saveTiffCoverage(fileName, coverage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static GridCoverage2D importRaster(File rasterIn) throws IOException {
//		 ParameterValue<OverviewPolicy> policy = AbstractGridFormat.OVERVIEW_POLICY.createValue();
//		 policy.setValue(OverviewPolicy.IGNORE);
//		 ParameterValue<String> gridsize = AbstractGridFormat.SUGGESTED_TILE_SIZE.createValue();
//		 ParameterValue<Boolean> useJaiRead = AbstractGridFormat.USE_JAI_IMAGEREAD.createValue();
//		 useJaiRead.setValue(true);
//		 GeneralParameterValue[] params = new GeneralParameterValue[] { policy, gridsize, useJaiRead };
//		 GridCoverage2DReader reader = new GeoTiffReader(rasterIn);
//		 GridCoverage2D coverage = reader.read(params);
//		 return coverage;
		return IOImage.loadTiff(rasterIn);
	}
}
