package fr.ign.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.grid.Grids;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;

import au.com.bytecode.opencsv.CSVReader;
import fr.ign.cogit.GTFunctions.Csv;
import fr.ign.cogit.GTFunctions.Rasters;
import fr.ign.cogit.GTFunctions.Vectors;

public class OutputTools {

	public static void main(String[] args) throws IOException, NoSuchAuthorityCodeException, FactoryException, ParseException {
		vectorizeMupOutput(Rasters.importRaster(new File(
				"/media/mcolomb/Data_2/simu/MupCityDepot/CDense/variantMvGrid1/CDense--MouvGrid3--N6_St_Moy_ahpE_seed_42-evalAnal-20.0.tif")),
				new File("/media/mcolomb/Data_2/simu/MupCityDepot/CDense/variantMvGrid1/CDense--MouvGrid3--N6_St_Moy_ahpE_seed_42-evalAnal-20.0.shp"), 20);
	}
	
	public static File getMupFileFromFolder(File mupFolder, String echelle) throws FileNotFoundException {
		for (File f : mupFolder.listFiles()) {
			if (f.getName().endsWith("evalAnal-"+echelle+".0.tif")) {
				return f;
			}
		}
		throw new FileNotFoundException();
	}

	public static File vectorizeMupOutput(GridCoverage2D coverage, File outFile, double sizeCell)
			throws IOException, NoSuchAuthorityCodeException, FactoryException, ParseException {

		CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:2154");
		ReferencedEnvelope gridBounds = new ReferencedEnvelope(coverage.getEnvelope2D().getMinX(), coverage.getEnvelope2D().getMaxX(), coverage.getEnvelope2D().getMinY(),
				coverage.getEnvelope2D().getMaxY(), sourceCRS);

		SimpleFeatureTypeBuilder sfTypeBuilder = new SimpleFeatureTypeBuilder();
		sfTypeBuilder.setName("vecorizedMupOutput");
		sfTypeBuilder.setCRS(sourceCRS);
		sfTypeBuilder.add("the_geom", Polygon.class);
		sfTypeBuilder.add("eval", Float.class);
		sfTypeBuilder.setDefaultGeometry("the_geom");

		SimpleFeatureBuilder sfBuilder = new SimpleFeatureBuilder(sfTypeBuilder.buildFeatureType());
		DefaultFeatureCollection output = new DefaultFeatureCollection();

		int i = 0;

		// TODO faire un flux avec cette crap (ça fait bugger mon petit ordi qui a pas
		// assez de mémoire) peut être aussi que c'est mal optimisé, que la grid devrait
		// être calculé une bonne fois pour toute et rester dans un objet dédié. J'ai
		// fait ça pour que moins de trucs en mémoire soit stocké et peut être pas avoir
		// l'erreure
		// SimpleFeatureCollection cellsGridSFC = Grids.createSquareGrid(gridBounds, sizeCell).getFeatures();
		// SimpleFeature[] salut = cellsGridSFC.toArray(new SimpleFeature[0]);
		// Stream<SimpleFeature> s = Arrays.stream(salut).filter(sf-> (((float[])(coverage.evaluate(new DirectPosition2D((sf.getBounds().getMaxX() - sf.getBounds().getHeight() /
		// 2),
		// (sf.getBounds().getMaxY() - sf.getBounds().getHeight() / 2))))[0])>0));
		// DefaultFeatureCollection geometryCollection = new DefaultFeatureCollection(Arrays.asList(s.toArray()));

		SimpleFeatureIterator featIt = Grids.createSquareGrid(gridBounds, sizeCell).getFeatures().features();

		try {
			while (featIt.hasNext()) {
				SimpleFeature feat = featIt.next();

				float yo = ((float[]) coverage.evaluate(new DirectPosition2D((feat.getBounds().getMaxX() - feat.getBounds().getWidth() / 2), (feat.getBounds().getMaxY() - feat.getBounds().getHeight() / 2))))[0];
						
				if (yo > 0) {
					i = i + 1;
					sfBuilder.add(feat.getDefaultGeometry());
					output.add(sfBuilder.buildFeature("id" + i, new Object[] { yo }));
				}
			}
		} catch (Exception problem) {
			problem.printStackTrace();
		} finally {
			featIt.close();
		}
		Vectors.exportSFC(output.collection(), outFile);
		return outFile;
	}

	/**
	 * Class to get all the fractal dimensions from the .csv files and copy them to a file named {f}/totalDimFractales.csv Eliminates the duplicated simulations
	 * 
	 * @param f
	 *            gile containing the different scenario files
	 * @param echelle
	 *            : scale of the different parcel sizes
	 * @throws IOException
	 */
	public static void digOnDimFract(File f, String echelle) throws IOException {
		List<String> finalLines = new ArrayList<String>();
		List<String> scenarNames = new ArrayList<String>();
		for (File fileScenar : f.listFiles()) {
			if (fileScenar.isDirectory() && fileScenar.getName().startsWith("data")) {
				for (File fileDimFract : fileScenar.listFiles()) {
					fileDimFract = new File(fileDimFract, "/dimensionFractale.csv");
					if (fileDimFract.exists()) {
						CSVReader csvRead = new CSVReader(new FileReader(fileDimFract));
						List<String[]> listLine = csvRead.readAll();
						for (int i = 0; i < listLine.size(); i++) {
							String[] line = listLine.get(i);
							if (line[0].equals("dimension de corrélation")) {
								if (listLine.get(i - 1)[0].endsWith("evalAnal-20.0.tif")) {

									String nametemp = listLine.get(i - 1)[0].replaceAll("scenario Stability-dataAutom-CM20.0-S0.0-GP_915948.0_6677337.0--", "");
									Pattern dbTiret = Pattern.compile("_");
									String[] decompName = dbTiret.split(nametemp);
									String name = (decompName[0] + "_" + decompName[1] + "_" + decompName[2]);
									// eliminates the duplications
									String scenarName = name + decompName[5];
									if (!scenarNames.contains(scenarName)) {
										finalLines.add(name + "," + line[1]);
										scenarNames.add(scenarName);
									}
								}
							}
						}
						csvRead.close();
					}
				}
			}

		}
		Csv.simpleCSVWriter(finalLines, new File(f, "totalDimFractales"), false);
	}
	
	public static String niceScenarName(int nmax, boolean strict, boolean mean, String ahpName, long seed) {
		
		String strStrict = "Ba";
		if (strict) {
			strStrict="St";
		}
		
		String result = "N"+nmax+"_"+strStrict+"_"+ahpName+"_"+"seed"+"_"+seed;
		
		
		
		return result;
	}

}
