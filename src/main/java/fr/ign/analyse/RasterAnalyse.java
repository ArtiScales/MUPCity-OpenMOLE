package fr.ign.analyse;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.grid.Grids;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import au.com.bytecode.opencsv.CSVReader;
import fr.ign.analyse.obj.Analyse;
import fr.ign.analyse.obj.ScenarAnalyse;
import fr.ign.analyse.obj.ScenarAnalyseFile;
import fr.ign.cogit.GTFunctions.Csv;
import fr.ign.cogit.GTFunctions.Rasters;
import fr.ign.tools.ScenarTools;
import fr.ign.tools.StatTab;

public class RasterAnalyse {

	/**
	 * This class contains several methods used for the analysis of the MUP-City outputs during the sensibility and stability tests raster outputs must contains the selected to
	 * urbanize cells mixed with the evaluation layer (output of the extract-eval-anal method) The raster selected with the selectWith method are compared within the mergeRaster
	 * method There is two ways to compare rasters : if they are composed of the exact same grid, we will use the relative position of the cells within this grid. The "discrete"
	 * variable will be "false" and CreateStats method will be used to calculate statistics if the rasters to compare are different, we use the DirectPosition object to locate the
	 * cells. The "discrete" variable will be "true" and SplitMergedTypo method will be used to calculate statistics
	 * 
	 */

	public static File rootFile;
	public static File statFile;
	public static boolean stabilite = false;
	public static boolean cutBorder = false;
	// if cutBorder true, then there's a grid move and the middle grid is needed
	public static File middleGridRaster;

	public static String echelle;
	public static boolean firstline = true;
	public static boolean saveEvalTab = false;

	public static void main(String[] args) throws Exception {
		rootFile = new File("/media/mcolomb/Data_2/resultFinal/testAHP/comparaison/compAHP-Autom-CM20.0-S0.0-GP_915948.0_6677337.0");
		echelle = "20";


		// makeComparaisonDecFract(new File("/media/mcolomb/Data_2/resultFinal/scenarios/decompFract"), new File("/media/mcolomb/Data_2/dataOpenMole/all/discreteFile.shp"), "20");
	}

	public static void makeComparaisonDecFract(File mainFile, File discreteFile, String echellee) throws IOException {

		Hashtable<String, double[]> resultTable = new Hashtable<String, double[]>();
		String[] fLine = { "Paramètres", "Nombre de cellule totale", "Nombre de cellules dans la typologie rurale", "Nombre de cellules dans la typologie péri-urbaine",
				"Nombre de cellules dans la typologie banlieue", "Nombre de cellules dans la typologie centre-ville", "zone urbanisée", "zone à urbaniser" };
		echelle = echellee;
		String typoObjects = "typo";
		String[] diffObject = { "allIn", "rural", "peri-urbain", "banlieue", "hypercentre", "peri-centre" };

		File zoneFile = new File("/media/mcolomb/Data_2/dataOpenMole/all/zonage.shp");
		String zoneObjects = "TYPEZONE";
		String[] zoneObject = { "AU", "U", "ZC" };

		for (File f : mainFile.listFiles()) {
			if (f.isDirectory() && f.getName().startsWith("N")) {
				for (File ff : f.listFiles()) {
					if (ff.getName().endsWith("evalAnal-" + echelle + ".0.tif")) {
						double[] line = new double[fLine.length - 1];
						Hashtable<String, Integer> typo = getDiscreteCharacteristic(ff, discreteFile, typoObjects, diffObject);
						line[0] = typo.get("allIn");
						line[1] = typo.get("rural");
						line[2] = typo.get("peri-urbain");
						line[3] = typo.get("banlieue");
						line[4] = typo.get("hypercentre") + typo.get("peri-centre");

						Hashtable<String, Integer> zone = getDiscreteCharacteristic(ff, zoneFile, zoneObjects, zoneObject);
						line[5] = zone.get("U") + zone.get("ZC");
						line[6] = zone.get("AU");

						resultTable.put(f.getName().split("_")[0] + f.getName().split("_")[1], line);
					}
				}
			}
		}

		Csv.generateCsvFile(resultTable, mainFile, "comparaisonTypo", fLine);
	}

	/**
	 * get discrete characteristic from a single MUP-City's outupt
	 * 
	 * @param nameScenar
	 *            : name of the output file
	 * @param rasterFile
	 *            : MUP-City's output
	 * @param discreteFile
	 *            : shapeFile containing the discretization
	 * @param field
	 *            : filed to discretize with
	 * @return a list containing the number of the wanted filed
	 * @throws IOException
	 */
	public static Hashtable<String, Integer> getDiscreteCharacteristic(File rasterFile, File discreteFile, String field, String[] wantedFeat) throws IOException {
		Hashtable<String, Integer> result = new Hashtable<String, Integer>();
		for (String w : wantedFeat) {
			result.put(w, 0);
		}
		ShapefileDataStore fabricSDS = new ShapefileDataStore(discreteFile.toURI().toURL());
		SimpleFeatureCollection fabricType = fabricSDS.getFeatureSource().getFeatures();

		SimpleFeatureIterator iteratorGeoFeat = fabricType.features();

		try {
			// Pour toutes les entitées
			while (iteratorGeoFeat.hasNext()) {
				SimpleFeature city = iteratorGeoFeat.next();
				String fabricName = (String) city.getAttribute(field);
				// does tab contained the selected feature?
				boolean concerned = false;
				for (String s : wantedFeat) {
					if (s.equals(fabricName)) {
						concerned = true;
						break;
					}
				}
				if (concerned) {
					// pour toutes les cellules
					GridCoverage2D coverageMup = Rasters.importRaster(rasterFile, (Geometry) city.getDefaultGeometry());
					if (coverageMup != null) {
						Envelope2D env = coverageMup.getEnvelope2D();
						double Xmin = env.getMinX();
						double Xmax = env.getMaxX();
						double Ymin = env.getMinY();
						double Ymax = env.getMaxY();
						double ech = Double.valueOf(echelle);
						for (double r = Xmin + ech / 2; r <= Xmax; r = r + ech) {
							// those values are the bounds from project (and upped to correspond to a
							// multiple of 180 to analyse all the cells in the project)
							for (double t = Ymin + ech / 2; t <= Ymax; t = t + ech) {
								DirectPosition2D coordCentre = new DirectPosition2D(r, t);
								float[] cellMup = (float[]) coverageMup.evaluate(coordCentre);
								if (cellMup[0] > 0) {
									result.put(fabricName, result.get(fabricName) + 1);
									if (result.containsKey("allIn")) {
										result.put("allIn", result.get("allIn") + 1);
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception problem) {
			problem.printStackTrace();
		} finally {
			iteratorGeoFeat.close();
		}

		fabricSDS.dispose();
		return result;
	}

	/**
	 * Select a list of file with the argument "with" in its name from the rootFile
	 *
	 * @param with
	 *            : String that is contained into the selection's name
	 * @param in:
	 *            optional , if the search is needed to be in a specific list
	 * @return an arrayList of files
	 * @author Maxime Colomb
	 * @throws Exception
	 * 
	 */
	public static ArrayList<File> selectWith(String with, ArrayList<File> in) throws IOException {
		ArrayList<File> listFile = new ArrayList<File>();
		if (in == null) {
			for (File fil : rootFile.listFiles()) {
				Pattern ech = Pattern.compile("evalAnal-");
				String[] list = ech.split(fil.toString());
				if (fil.toString().contains(with) && list.length > 1 && list[1].equals(echelle + ".0.tif")) {
					listFile.add(fil);
				}

			}
		} else {
			for (File fil : in) {
				Pattern ech = Pattern.compile("evalAnal-");
				String[] list = ech.split(fil.toString());
				if (fil.toString().contains(with) && list[1].equals(echelle + ".0.tif")) {
					listFile.add(fil);
				}
			}
		}
		return listFile;
	}

	/**
	 * Count how many cells of 20m are included in cells of 180m
	 * 
	 * @author Maxime "proud" Colomb
	 * @param cellRepetCentroid:
	 * @param echelle:
	 *            scale of the file
	 * @param in:
	 *            array of file to search in (can be null)
	 * @return an ArrayList of File
	 * @throws Exception
	 * @throws IOException
	 */

	public static void compareInclusionSizeCell(Hashtable<DirectPosition2D, Integer> SvgCellRepet20, Hashtable<DirectPosition2D, Float> SvgCellEval20,
			Hashtable<DirectPosition2D, Integer> cellRepetParent, Hashtable<DirectPosition2D, Float> cellEvalParent, String namescenar, int echelle) throws IOException {

		// nb of cells
		int cellIn = 0;
		int cellOut = 0;
		int cellTotal = SvgCellRepet20.size();

		ArrayList<Float> cellInEval = new ArrayList<Float>();
		ArrayList<Float> cellOutEval = new ArrayList<Float>();
		// TODO it's a weird line
		Hashtable<DirectPosition2D, Float> doubleSvgCellEval20 = (Hashtable<DirectPosition2D, Float>) SvgCellEval20.clone();

		for (DirectPosition2D coord : cellRepetParent.keySet()) {
			double empXmin = coord.getX() - echelle / 2;
			double empXmax = coord.getX() + echelle / 2;
			double empYmin = coord.getY() - echelle / 2;
			double empYmax = coord.getY() + echelle / 2;
			for (DirectPosition2D coord20 : SvgCellRepet20.keySet()) {
				if (coord20.getX() > empXmin && coord20.getX() < empXmax && coord20.getY() > empYmin && coord20.getY() < empYmax) {
					cellIn = cellIn + 1;
					cellInEval.add(SvgCellEval20.get(coord20));
					doubleSvgCellEval20.remove(coord20);
				}
			}
		}

		for (DirectPosition2D cell : doubleSvgCellEval20.keySet()) {
			cellOut = cellOut + 1;
			cellOutEval.add(doubleSvgCellEval20.get(cell));
		}

		int cellOutTheorie = cellTotal - cellIn;

		// eval moyenne des cellules contenues
		float sumInVal = 0;
		for (float val : cellInEval) {
			sumInVal = sumInVal + val;
		}
		float averageValIn = sumInVal / cellInEval.size();

		// eval moyenne des cellules non contenues
		float sumOutVal = 0;
		for (float val : cellOutEval) {
			sumOutVal = sumOutVal + val;
		}
		float averageValOut = sumOutVal / cellOutEval.size();

		// eval moyenne des cellules totales
		float sumCellEval = 0;
		for (float val : SvgCellEval20.values()) {
			sumCellEval = sumCellEval + val;
		}
		float averageValTot = sumCellEval / SvgCellEval20.size();

		double[] resultStats = new double[6];
		String[] firstLine = new String[6];

		firstLine[0] = "Nombre totale de cellules";
		firstLine[1] = "evaluation moyenne de toutes les cellules";
		firstLine[2] = "Cellules de 20m non inclues dans les cellules de " + echelle + "m";
		firstLine[3] = "evaluation moyenne des cellules de 20m non inclues dans les cellules de " + echelle + "m";
		firstLine[4] = "Cellules de 20m inclues dans les cellules de " + echelle + "m";
		firstLine[5] = "evaluation moyenne des cellules de 20m incluses dans les cellules de " + echelle + "m";

		resultStats[0] = cellTotal;
		resultStats[1] = averageValTot;
		resultStats[2] = cellOut;
		resultStats[3] = averageValOut;
		resultStats[4] = cellIn;
		resultStats[5] = averageValIn;

		StatTab result = new StatTab("compare_20to" + echelle, (namescenar + "--compare-20/" + echelle), resultStats, firstLine);

		result.toCsv(statFile, true);

	}

	/**
	 * calculate the accessibility from multiple points using the evaluation raster already calculated by MUP-City. Write multiple statistical .csv file on the static rootFile
	 * folder
	 * 
	 * @param nameExplo
	 *            : the given name of your MUP-City's project
	 * @param isResultFile
	 *            : if the folder is organized as a analysis result one or as a normal one
	 * @throws NoSuchAuthorityCodeException
	 * @throws IOException
	 * @throws FactoryException
	 * @throws ParseException
	 */
	public static void getEvals(String nameExplo, boolean isResultFile) throws NoSuchAuthorityCodeException, IOException, FactoryException, ParseException {

		Hashtable<String, Hashtable<String, Double[]>> distServices = new Hashtable<String, Hashtable<String, Double[]>>();
		Hashtable<String, Hashtable<String, Double[]>> distLeisure = new Hashtable<String, Hashtable<String, Double[]>>();
		Hashtable<String, Hashtable<String, Double[]>> distTC = new Hashtable<String, Hashtable<String, Double[]>>();
		if (isResultFile) {
			for (File f : rootFile.listFiles()) {
				if (f.isDirectory() && f.getName().startsWith("result")) {
					for (File ff : f.listFiles()) {
						if (ff.getName().startsWith(nameExplo)) {
							for (File fff : ff.listFiles()) {
								if (fff.getName().equals("SortieExemple")) {
									for (File ffff : fff.listFiles()) {
										if (ffff.getName().endsWith("evalAnal-" + echelle + ".0.tif")) {
											String nameScenar = ffff.getName().split("--")[1].replace("evalAnal-" + echelle + ".0.tif", "");
											distServices.put(nameScenar, getDistanceFromServices(fff, nameScenar));
											distLeisure.put(nameScenar, getDistanceFromLeisure(fff, nameScenar));
											distTC.put(nameScenar, getDistanceFromTC(fff, nameScenar));
										}
									}
								}
							}
						}
					}
				}
			}
		} else {
			// for (File f : rootFile.listFiles()) {
			// if (f.isDirectory() && f.getName().startsWith(nameExplo)) {
			for (File ff : rootFile.listFiles()) {
				System.out.println(ff);
				if (ff.getName().startsWith("N")&& !ff.getName().contains("Yag")) {
					for (File fff : ff.listFiles()) {
						if (fff.getName().endsWith("evalAnal-" + echelle + ".0.tif")) {
							String nameScenar = fff.getName().replace("evalAnal-" + echelle + ".0.tif", "");
							distServices.put(nameScenar, getDistanceFromServices(fff, nameScenar));
							distLeisure.put(nameScenar, getDistanceFromLeisure(fff, nameScenar));
							distTC.put(nameScenar, getDistanceFromTC(fff, nameScenar));
						}
					}
				}
				// }
				// }

			}
		}
		Csv.generateCsvFileMultTab(distTC, nameExplo + "-distanceTC", "mean,standart deviation", rootFile);
		Csv.needFLine = true;
		Csv.generateCsvFileMultTab(distLeisure, nameExplo + "-distanceLeisure", "mean,standart deviation", rootFile);
		Csv.needFLine = true;
		Csv.generateCsvFileMultTab(distServices, nameExplo + "-distanceServices", "mean,standart deviation", rootFile);
	}

	/**
	 * calculate distances of Mup-City's outputs from frequency-hierarchized shops and services
	 * 
	 * @param mupOutputFile
	 * @param nameEval
	 * @return a Double tableau containing [0] mean and [1] standard deviation of the distances
	 * @throws IOException
	 */
	public static Hashtable<String, Double[]> getDistanceFromLeisure(File mupOutputFile, String nameScenario)
			throws NoSuchAuthorityCodeException, IOException, FactoryException, ParseException {

		Hashtable<String, Double[]> result = new Hashtable<String, Double[]>();

		String[] nameFac = { "lei1", "lei2", "lei3" };

		for (String name : nameFac) {
			result.put(nameScenario + "-" + name, getDistanceFromDot(mupOutputFile, name));
		}
		return result;
	}

	/**
	 * calculate distances of Mup-City's outputs from frequency-hierarchized shops and services
	 * 
	 * @param mupOutputFile
	 * @param nameEval
	 * @return a Double tableau containing [0] mean and [1] standard deviation of the distances
	 * @throws IOException
	 */
	public static Hashtable<String, Double[]> getDistanceFromServices(File mupOutputFile, String nameScenario)
			throws NoSuchAuthorityCodeException, IOException, FactoryException, ParseException {

		Hashtable<String, Double[]> result = new Hashtable<String, Double[]>();

		String[] nameFac = { "fac1", "fac2", "fac3" };

		for (String name : nameFac) {
			result.put(nameScenario + "-" + name, getDistanceFromDot(mupOutputFile, name));
		}
		return result;
	}

	/**
	 * calculate distances of Mup-City's outputs from public transports
	 * 
	 * @param networkFile
	 * @param buildFile
	 * @param mupOutputFile
	 * @param echelle
	 * @return a Double tableau containing [0] mean and [1] standard deviation of the distances
	 * @throws NoSuchAuthorityCodeException
	 * @throws IOException
	 * @throws FactoryException
	 * @throws ParseException
	 */
	public static Hashtable<String, Double[]> getDistanceFromTC(File mupOutputFile, String nameScenario)
			throws NoSuchAuthorityCodeException, IOException, FactoryException, ParseException {

		Hashtable<String, Double[]> result = new Hashtable<String, Double[]>();

		String[] nameFac = { "pt" };

		for (String name : nameFac) {
			result.put(nameScenario + "," + name, getDistanceFromDot(mupOutputFile, name));
		}
		return result;
	}

	/**
	 * calculate distances of Mup-City's outputs from divers kind of ponctual objects based on MUP-City's evaluation rasters
	 * 
	 * @param mupOutputFile
	 * @param nameEval
	 * @return a Double tableau containing [0] mean and [1] standard deviation of the distances
	 * @throws IOException
	 */
	public static Double[] getDistanceFromDot(File mupOutputFile, String nameEval) throws IOException {

		File gridFoler = ScenarTools.getGridFolderWhereEver(rootFile);

		DescriptiveStatistics facDS = new DescriptiveStatistics();

		GridCoverage2D coverageMup = Rasters.importRaster(mupOutputFile);

		GridCoverage2D coverageServiceQuot = Rasters.importRaster(new File(gridFoler, nameEval + "-" + echelle + ".0.tif"));
		Envelope2D env = coverageMup.getEnvelope2D();
		double Xmin = env.getMinX();
		double Xmax = env.getMaxX();
		double Ymin = env.getMinY();
		double Ymax = env.getMaxY();
		double ech = Double.valueOf(echelle);
		for (double r = Xmin + ech / 2; r <= Xmax; r = r + ech) {
			// those values are the bounds from project (and upped to correspond to a
			// multiple of 180 to analyse all the cells in the project)
			for (double t = Ymin + ech / 2; t <= Ymax; t = t + ech) {
				DirectPosition2D coordCentre = new DirectPosition2D(r, t);
				float[] cellMup = (float[]) coverageMup.evaluate(coordCentre);
				if (cellMup[0] > 0) {
					facDS.addValue(((float[]) coverageServiceQuot.evaluate(coordCentre))[0]);
				}
			}
		}
		Double[] result = new Double[2];
		result[0] = facDS.getMean();
		result[1] = facDS.getStandardDeviation();

		return result;
	}

	/**
	 * Vectorize a MUP-City output
	 * 
	 * @param coverage
	 *            : raster file of the MUP-City Output
	 * @param cellSize
	 *            : resolution of the cells
	 * @return : A collection of vectorized cells
	 * @throws IOException
	 * @throws NoSuchAuthorityCodeException
	 * @throws FactoryException
	 * @throws ParseException
	 */
	public static SimpleFeatureCollection createMupOutput(GridCoverage2D coverage, int cellSize)
			throws IOException, NoSuchAuthorityCodeException, FactoryException, ParseException {

		CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:2154");
		ReferencedEnvelope gridBounds = new ReferencedEnvelope(coverage.getEnvelope2D().getMinX(), coverage.getEnvelope2D().getMaxX(), coverage.getEnvelope2D().getMinY(),
				coverage.getEnvelope2D().getMaxY(), sourceCRS);

		WKTReader wktReader = new WKTReader();
		SimpleFeatureTypeBuilder sfTypeBuilder = new SimpleFeatureTypeBuilder();

		sfTypeBuilder.setName("testType");
		sfTypeBuilder.setCRS(sourceCRS);
		sfTypeBuilder.add("the_geom", Polygon.class);
		sfTypeBuilder.setDefaultGeometry("the_geom");
		sfTypeBuilder.add("eval", Float.class);

		SimpleFeatureType featureType = sfTypeBuilder.buildFeatureType();
		SimpleFeatureBuilder sfBuilder = new SimpleFeatureBuilder(featureType);

		DefaultFeatureCollection victory = new DefaultFeatureCollection();

		SimpleFeatureSource grid = Grids.createSquareGrid(gridBounds, cellSize);

		int i = 0;
		SimpleFeatureIterator featureIt = grid.getFeatures().features();
		try {
			while (featureIt.hasNext()) {
				SimpleFeature feat = featureIt.next();
				DirectPosition2D coord = new DirectPosition2D((feat.getBounds().getMaxX() - feat.getBounds().getHeight() / 2),
						(feat.getBounds().getMaxY() - feat.getBounds().getHeight() / 2));
				float[] yo = (float[]) coverage.evaluate(coord);
				if (yo[0] > 0) {
					i = i + 1;
					Object[] attr = { yo[0] };
					sfBuilder.add(wktReader.read(feat.getDefaultGeometry().toString()));
					SimpleFeature feature = sfBuilder.buildFeature("id" + i, attr);
					victory.add(feature);
				}
			}
		} catch (Exception problem) {
			problem.printStackTrace();
		} finally {
			featureIt.close();
		}
		// exportSFC(victory.collection(), new File("home/mcolomb/tmp/outMupEx.shp"));
		return victory.collection();
	}

	/**
	 * Write how much surface are included into each cities
	 * 
	 * @param listfile
	 * @param nameScenar
	 * @param discreteFile
	 *            : the file to make the discretization. Must contain a "NOM_COM" field
	 * @return
	 * @throws Exception
	 */
	public static void compareSameSizedCellIntoCities(Set<ScenarAnalyse> listfile, Analyse anal, String name, File discreteFile) throws Exception {

		// with the other parameters
		// with the topological spaces
		Hashtable<String, String[]> tabDifferentObjects = loopDiffEntities();

		// pour tous les scénarios de la liste - comprenant les mêmes scenarios et
		// projets mais avec des tailles de cellules différentes
		for (ScenarAnalyse sA : listfile) {

			String[] firstCol = new String[listfile.size() * 2 + 1];

			int scenar = 1;
			echelle = sA.getSizeCell();
			File file2Anal = anal.getSimuFile(sA, echelle, "evalAnal");
			firstCol[scenar] = "echelle - " + echelle;
			scenar = scenar + 2;
			int size = Integer.valueOf(echelle);

			SimpleFeatureCollection output = createMupOutput(Rasters.importRaster(file2Anal), size);

			for (String[] differentObject : tabDifferentObjects.values()) {
				firstCol[0] = differentObject[0];
				ShapefileDataStore discreteSDS = new ShapefileDataStore(discreteFile.toURI().toURL());
				SimpleFeatureCollection discrete = discreteSDS.getFeatureSource().getFeatures();
				Hashtable<String, double[]> result = new Hashtable<>();

				// instanciation of the different entities in the result
				SimpleFeatureIterator discreteIt = discrete.features();
				try {
					while (discreteIt.hasNext()) {
						String city = (String) (discreteIt.next()).getAttribute(differentObject[1]);
						result.put(city, new double[0]);
					}
				} catch (Exception problem) {
					problem.printStackTrace();
				} finally {
					discreteIt.close();
				}
				Hashtable<String, Double> entitySurf = new Hashtable<String, Double>();
				Hashtable<String, Double> entityNumber = new Hashtable<String, Double>();
				SimpleFeatureIterator discreteIt2 = discrete.features();
				try {
					while (discreteIt2.hasNext()) {
						SimpleFeature city = discreteIt2.next();
						String entityName = (String) city.getAttribute(differentObject[1]);
						SimpleFeatureIterator outputIt = output.features();
						try {
							while (outputIt.hasNext()) {
								SimpleFeature cell = outputIt.next();
								if (((Geometry) city.getDefaultGeometry()).intersects((Geometry) cell.getDefaultGeometry())) {
									double surf = ((Geometry) city.getDefaultGeometry()).intersection((Geometry) cell.getDefaultGeometry()).getArea();
									if (entitySurf.containsKey(entityName)) {
										double temp = entitySurf.get(entityName) + surf;
										entitySurf.remove(entityName);
										entitySurf.put(entityName, temp);

										double tempNb = entityNumber.get(entityName) + 1;
										entityNumber.remove(entityName);
										entityNumber.put(entityName, tempNb);

									} else {
										entitySurf.put(entityName, surf);
										entityNumber.put(entityName, (double) 1);
									}
								}
							}
						} catch (Exception problem) {
							problem.printStackTrace();
						} finally {
							outputIt.close();
						}
					}
				} catch (Exception problem) {
					problem.printStackTrace();
				} finally {
					discreteIt2.close();
				}

				// put the values into the right case
				for (String entity : result.keySet()) {
					double[] temp = result.get(entity);
					double[] toPut = new double[temp.length + 2];
					// ça recopie
					if (temp.length > 0) {
						for (int i = 0; i < temp.length; i++) {
							toPut[i] = temp[i];
						}
					}
					if (entitySurf.containsKey(entity)) {
						toPut[temp.length] = entitySurf.get(entity);
						toPut[temp.length + 1] = entityNumber.get(entity);
					} else {
						toPut[temp.length] = 0;
						toPut[temp.length + 1] = 0;
					}
					result.put(entity, toPut);
				}

				Csv.generateCsvFile(result, statFile, name + "-surfaceOfCells" + "-" + differentObject[0], firstCol);
				discreteSDS.dispose();
			}
		}
	}

	public static RasterMergeResult mergeRasters(List<ScenarAnalyse> listSA, Analyse anal, ScenarAnalyse justToOverload) throws Exception {
		List<File> inList = new ArrayList<File>();
		for (ScenarAnalyseFile sAf : anal.fileCollec) {
			for (ScenarAnalyse sA : listSA) {
				if (sAf.equals(sA) && sAf.getEchelle().equals("20") && sAf.getMeaning().equals("eval-anal")) {
					inList.add(sAf.getFileFile());
				}
			}
		}
		return mergeRasters(inList);
	}

	/**
	 * Overload to praticcaly put a single raster into a RasterMergeResult format
	 * 
	 * @param f
	 * @return
	 * @throws Exception
	 */
	public static RasterMergeResult mergeRasters(File f) throws Exception {
		List<File> singleList = new ArrayList<File>();
		singleList.add(f);
		return mergeRasters(singleList);
	}

	/**
	 * mergeRaster Merge the given list of MUP-City's output regarding to a grid.
	 * 
	 * @param listRepliFile
	 *            : ArrayList of File pointing to the raster layer to merge
	 * 
	 * @return RasterMergeResult object
	 * @throws Exception
	 */
	public static RasterMergeResult mergeRasters(List<File> listRepliFile) throws Exception {
		System.out.println("merging " + listRepliFile.size() + " rasters");
		// variables to create statistics

		DescriptiveStatistics statNb = new DescriptiveStatistics();
		Hashtable<DirectPosition2D, Integer> cellRepetCentroid = new Hashtable<DirectPosition2D, Integer>();
		Hashtable<DirectPosition2D, ArrayList<Float>> cellEvalCentroid = new Hashtable<DirectPosition2D, ArrayList<Float>>();

		int nbDeScenar = 0; // le nombre total de scénarios analysés dans la fonction

		double[] histo = new double[listRepliFile.size()];
		int iter = 0;

		// variables for merged raster
		// not cool coz i cannot know the number of column and lines of the enveloppe
		// yet and the type need it
		// change the type to a collection or an arraylist?

		Envelope2D env = null;

		// loop on the different cells
		for (File f : listRepliFile) {
			GridCoverage2D coverage = Rasters.importRaster(f);

			if (env == null) {
				if (cutBorder) {
					env = Rasters.importRaster(middleGridRaster).getEnvelope2D();
				} else {
					env = coverage.getEnvelope2D();
				}
			}

			int compteurNombre = 0;
			nbDeScenar = nbDeScenar + 1;

			// in case of a move of the grid, we have to delete the border cells because
			// they will be moved

			double Xmin = env.getMinX();
			double Xmax = env.getMaxX();
			double Ymin = env.getMinY();
			double Ymax = env.getMaxY();
			if (cutBorder == true) {
				int ecart = Integer.parseInt(echelle);
				Xmin = Xmin + ecart;
				Xmax = Xmax - ecart;
				Ymin = Ymin + ecart;
				Ymax = Ymax - ecart;
			}
			for (double r = Xmin + Double.parseDouble(echelle) / 2; r <= Xmax; r = r + Double.parseDouble(echelle)) {
				// those values are the bounds from project (and upped to correspond to a
				// multiple of 180 to analyse all the cells in the project)
				for (double t = Ymin + Double.parseDouble(echelle) / 2; t <= Ymax; t = t + Double.parseDouble(echelle)) {
					DirectPosition2D coordCentre = new DirectPosition2D(r, t);
					float val = 0;
					try {
						val = ((float[]) coverage.evaluate(coordCentre))[0];
					} catch (ClassCastException c) {
						double zob = ((double[]) coverage.evaluate(coordCentre))[0];
						val = (float) zob;
					}

					if (val > 0) {
						compteurNombre++;
						if (cellRepetCentroid.containsKey(coordCentre)) { // si la cellule a déja été sélectionné lors
																			// de réplications
							cellRepetCentroid.put(coordCentre, cellRepetCentroid.get(coordCentre) + 1);
							ArrayList<Float> temp = cellEvalCentroid.get(coordCentre); // on mets les valeurs
																						// d'évaluation dans un tableau
							temp.add(val);
							cellEvalCentroid.put(coordCentre, temp);
						} else { // si la cellule est sélectionné pour la première fois
							cellRepetCentroid.put(coordCentre, 1);
							ArrayList<Float> firstList = new ArrayList<Float>();
							firstList.add(val);
							cellEvalCentroid.put(coordCentre, firstList);
						}
					}
				}
			}
			System.out.println("il y a " + compteurNombre + " cellules sur " + cellRepetCentroid.size() + " dans la réplication " + nbDeScenar);
			System.out.println();
			// Historique de l'évolution du nombre de cellules sélectionnées dans toutes les
			// simulations
			statNb.addValue(compteurNombre);
			histo[iter] = (double) cellRepetCentroid.size();
			iter = iter + 1;
		}

		Hashtable<DirectPosition2D, Float> cellEvalFinal = moyenneEvals(cellEvalCentroid);

		RasterMergeResult result = new RasterMergeResult();

		// could be heavy and unnecessary
		if (saveEvalTab) {
			result.setCellEvals(cellEvalCentroid);
		}

		result.setCellRepet(cellRepetCentroid);
		result.setCellEval(cellEvalFinal);
		result.setHistoDS(statNb);
		result.setHisto(histo);
		result.setNbScenar(nbDeScenar);
		return result;
	}

	/**
	 * automation of the discrete file treatment
	 * 
	 * @return
	 */
	public static Hashtable<String, String[]> loopDiffEntities() {
		Hashtable<String, String[]> tabDifferentObjects = new Hashtable<String, String[]>();
		String[] differentObjects = { "UrbanFabric", "typo" };
		tabDifferentObjects.put(differentObjects[0], differentObjects);
		String[] differentObjects2 = { "Morphology", "morpholo" };
		tabDifferentObjects.put(differentObjects2[0], differentObjects2);
		String[] differentObjects3 = { "Cities", "NOM_COM" };
		tabDifferentObjects.put(differentObjects3[0], differentObjects3);
		String[] differentObjects4 = { "MorphoCities", "morphocity" };
		tabDifferentObjects.put(differentObjects4[0], differentObjects4);
		return tabDifferentObjects;
	}

	public static File createStatDiscreteSingleSimu(String nameScenar, File mupOutputFile, File discreteFile) throws IOException {
		Hashtable<String, String[]> tabDifferentObjects = loopDiffEntities();

		// loop on those different objects
		for (String[] differentObject : tabDifferentObjects.values()) {
			createStatDiscreteSingleSimu(nameScenar, mupOutputFile, discreteFile, differentObject);
		}
		return statFile;
	}

	/**
	 * create the statistics for a discretized study
	 * 
	 * @param nameScenar
	 *            : name given to the study
	 * @param cellRepet
	 *            : Collection of the cell's replication
	 * @param cellEval
	 *            : Collection of the cell's evaluation
	 * @param champ
	 *            : containing [0] the name of the type of entites for the analyze and [1] its field form the attribute table
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static File createStatDiscreteSingleSimu(String nameScenar, File mupOutputFile, File discreteFile, String[] champ) throws IOException {

		String[] nameLineFabric = new String[5];
		nameLineFabric[0] = champ[0] + " name - echelle " + echelle + "scenar" + nameScenar;
		nameLineFabric[1] = "total Cells";
		nameLineFabric[2] = "surface Cells";
		nameLineFabric[3] = "average evaluation";
		nameLineFabric[4] = "standard deviation of evaluation";

		Hashtable<String, double[]> cellByFabric = new Hashtable<String, double[]>();
		Hashtable<String, DescriptiveStatistics> descStatByFabric = new Hashtable<String, DescriptiveStatistics>();
		System.out.println("pour le sujet " + champ[0]);
		ShapefileDataStore fabricSDS = new ShapefileDataStore(discreteFile.toURI().toURL());
		SimpleFeatureCollection fabricType = fabricSDS.getFeatureSource().getFeatures();

		GeometryFactory factory = new GeometryFactory();
		SimpleFeatureIterator iteratorGeoFeat = fabricType.features();

		GridCoverage2D rasterResult = Rasters.importRaster(mupOutputFile);

		double ech = Double.valueOf(echelle);

		try {
			// Pour toutes les entitées
			while (iteratorGeoFeat.hasNext()) {
				SimpleFeature city = iteratorGeoFeat.next();
				String cityName = (String) city.getAttribute(champ[1]);
				double[] resultFabric = new double[4];
				DescriptiveStatistics stat = new DescriptiveStatistics();
				// for the dimension of the geographic feature, we dig on mupcity's output
				Envelope env = ((Geometry) city.getDefaultGeometry()).getEnvelopeInternal();

				double Xmin = env.getMinX();
				double Xmax = env.getMaxX();
				double Ymin = env.getMinY();
				double Ymax = env.getMaxY();
				// if the geographic feature has already been analysed
				if (cellByFabric.contains(cityName)) {
					resultFabric = cellByFabric.get(cityName);
					stat = descStatByFabric.get(cityName);
				}
				// if the geo feature is contained into mupcity's output emprise
				Envelope2D envMup = rasterResult.getEnvelope2D();

				if (envMup.contains(Xmin, Ymin, Xmax - Xmin, Ymax - Ymin)) {

					for (double r = Xmin + ech / 2; r <= Xmax; r = r + ech) {
						for (double t = Ymin + ech / 2; t <= Ymax; t = t + ech) {

							DirectPosition2D coordCentre = new DirectPosition2D(r, t);
							float[] cellMup = (float[]) rasterResult.evaluate(coordCentre);
							if (cellMup[0] > 0) {
								if (((Geometry) city.getDefaultGeometry()).covers(factory.createPoint(new Coordinate(coordCentre.getX(), coordCentre.getY())))) {
									resultFabric[0] = resultFabric[0] + 1;
									stat.addValue(cellMup[0]);
								}
							}
						}
					}
					// area
					resultFabric[1] = resultFabric[0] * ech * ech;
					resultFabric[2] = stat.getMean();
					resultFabric[3] = stat.getStandardDeviation();
					// put nothing if it's empty
					// if (resultFabric[0]>0) {
					cellByFabric.put(cityName, resultFabric);
					// }
				}
			}
		} catch (Exception problem) {
			problem.printStackTrace();
		} finally {
			iteratorGeoFeat.close();
		}
		Csv.generateCsvFile(cellByFabric, statFile, ("cellBy" + champ[0]), nameLineFabric, false);
		fabricSDS.dispose();

		return statFile;
	}

	public static void prepareForCityComparison(File f, String scenarName) throws IOException {
		String[] firstLine = { "nameCommune" };
		Hashtable<String, double[]> result = new Hashtable<String, double[]>();
		int nbSim = 1;
		for (File resultFile : f.listFiles()) {
			if (resultFile.getName().endsWith(scenarName)) {
				String echelle = resultFile.getName().split("CM")[1].substring(0, 2);
				CSVReader csvRead = new CSVReader(new FileReader(new File(resultFile, "stat/cellByCities.csv")));
				// completet first line
				String[] oldFirstLine = firstLine;

				firstLine = new String[nbSim + 1];
				for (int i = 0; i < oldFirstLine.length; i++) {
					firstLine[i] = oldFirstLine[i];
				}
				firstLine[nbSim] = echelle;
				nbSim++;
				csvRead.readNext();
				for (String[] row : csvRead.readAll()) {

					if (result.containsKey(row[0])) {
						double[] res = result.remove(row[0]);
						int nbcol = res.length + 1;
						double[] newVal = new double[nbcol];
						for (int i = 0; i < res.length; i++) {
							newVal[i] = res[i];
						}
						newVal[nbcol - 1] = Double.valueOf(row[2]);
						result.put(row[0], newVal);
					} else {
						double[] newTab = { Double.valueOf(row[2]) };
						result.put(row[0], newTab);
					}
				}
				csvRead.close();
			}
		}
		Csv.generateCsvFile(result, f, "allCitiesFor-" + scenarName, firstLine);
		return;
	}

	/**
	 * analyse Mup-city's outputs with different vector geographic objects, all presented in the classic "discret file" with predefined name and fields
	 * 
	 * @return String[] with 0 : the type of entity and 1 : the attribute name
	 */
	public static File createStatsStabDiscrete(String nameScenar, RasterMergeResult result, File discreteFile) throws IOException {
		Hashtable<String, String[]> tabDifferentObjects = loopDiffEntities();

		// loop on those different objects
		for (String[] differentObject : tabDifferentObjects.values()) {
			createStatsStabDiscrete(nameScenar, result, discreteFile, differentObject);
		}
		return statFile;
	}

	/**
	 * create the statistics for a discretized study about stability
	 * 
	 * @param nameScenar
	 *            : name given to the study
	 * @param cellRepet
	 *            : Collection of the cell's replication
	 * @param cellEval
	 *            : Collection of the cell's evaluation
	 * @param champ
	 *            : containing [0] the name of the type of entites for the analyze and [1] its field form the attribute table
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static File createStatsStabDiscrete(String nameScenar, RasterMergeResult result, File discreteFile, String[] champ) throws IOException {

		String[] nameLineFabric = new String[5];
		nameLineFabric[0] = champ[0] + " name - echelle " + echelle + "scenar" + nameScenar;
		nameLineFabric[1] = "Total Cells";
		nameLineFabric[2] = "Stable cells";
		nameLineFabric[3] = "Unstable cells";
		nameLineFabric[4] = "average evaluation";

		Hashtable<String, double[]> cellByFabric = new Hashtable<String, double[]>();
		Hashtable<String, List<Double>> evals = new Hashtable<String, List<Double>>();

		System.out.println("pour le sujet " + champ[0]);
		ShapefileDataStore fabricSDS = new ShapefileDataStore(discreteFile.toURI().toURL());
		SimpleFeatureCollection fabricType = fabricSDS.getFeatureSource().getFeatures();

		GeometryFactory factory = new GeometryFactory();
		SimpleFeatureIterator iteratorGeoFeat = fabricType.features();

		try {
			// Pour toutes les entitées
			while (iteratorGeoFeat.hasNext()) {
				SimpleFeature city = iteratorGeoFeat.next();
				double[] resultFabric = new double[4];
				String fabricName = (String) city.getAttribute(champ[1]);
				// pour toutes les cellules
				for (DirectPosition2D coordCell : result.getCellRepet().keySet()) {
					if (((Geometry) city.getDefaultGeometry()).covers(factory.createPoint(new Coordinate(coordCell.getX(), coordCell.getY())))) {
						// si le tissus a déja été implémenté
						if (cellByFabric.containsKey(fabricName)) {
							double[] resultFabricPast = cellByFabric.get(fabricName);
							resultFabric[0] = resultFabricPast[0] + 1;
							// si la cellule est stable
							if (result.getCellRepet().get(coordCell) == result.getNbScenar()) {
								resultFabric[1] = resultFabricPast[1] + 1;
								resultFabric[2] = resultFabricPast[2];
							}
							// ou non
							else {
								resultFabric[2] = resultFabricPast[2] + 1;
								resultFabric[1] = resultFabricPast[1];
							}
							cellByFabric.put(fabricName, resultFabric);
						}
						// si le tissus n'as jamais été implémenté
						else {
							resultFabric[0] = (double) 1;
							// si la cellule est stable
							if (result.getCellRepet().get(coordCell) == result.getNbScenar()) {
								resultFabric[1] = (double) 1;
								resultFabric[2] = (double) 0;
							}
							// ou non
							else {
								resultFabric[2] = (double) 1;
								resultFabric[1] = (double) 0;
							}
							cellByFabric.put(fabricName, resultFabric);
						}
						// pour calculer les évaluations
						// on fait une liste vide
						List<Double> salut = new ArrayList<>();
						// si l'entité possède déjà une liste d'évaluations, on la récupère à la place
						if (evals.contains(fabricName)) {
							salut = evals.get(fabricName);
						}
						// on ajoute cette nouvelle évaluation
						salut.add((double) result.getCellEval().get(coordCell));
						// on la remet dans notre collection
						evals.put(fabricName, salut);
					}
				}
			}
		} catch (Exception problem) {
			problem.printStackTrace();
		} finally {
			iteratorGeoFeat.close();
		}
		// put the evals in
		for (String fabricName : evals.keySet()) {
			double sum = 0;
			int tot = 0;
			for (double db : evals.get(fabricName)) {
				sum = +db;
				tot++;
			}
			double[] finalle = cellByFabric.get(fabricName);
			finalle[3] = sum / tot;
			cellByFabric.put(fabricName, finalle);
		}

		Csv.generateCsvFile(cellByFabric, statFile, ("cellBy" + champ[0]), nameLineFabric);
		fabricSDS.dispose();

		return statFile;
	}

	public static File createCellsPerCities(String nameScenar, List<File> fileRepli, File discreteFile) throws IOException {
		return createCellsPerCities(nameScenar, fileRepli, discreteFile, false);
	}

	/**
	 * create the statistics for a discretized study
	 * 
	 * @param nameScenar
	 *            : name given to the study
	 * @param cellRepet
	 *            : Collection of the cell's replication
	 * @param cellEval
	 *            : Collection of the cell's evaluation
	 * @param champ
	 *            : containing [0] the name of the type of entites for the analyze and [1] its field form the attribute table
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static File createCellsPerCities(String nameScenar, List<File> fileRepli, File discreteFile, boolean surf) throws IOException {

		String[] nameLineFabric = new String[fileRepli.size() + 3];
		nameLineFabric[0] = "Cities - echelle " + echelle + "scenar" + nameScenar;
		nameLineFabric[1] = "coefficient de variation";

		for (int i = 2; i < fileRepli.size() + 2; i++) {
			nameLineFabric[i] = (i - 1) + " replication";
		}
		Hashtable<String, double[]> cellsByCity = new Hashtable<String, double[]>();

		ShapefileDataStore fabricSDS = new ShapefileDataStore(discreteFile.toURI().toURL());
		SimpleFeatureCollection fabricType = fabricSDS.getFeatureSource().getFeatures();

		GeometryFactory factory = new GeometryFactory();
		SimpleFeatureIterator iteratorCity = fabricType.features();
		double ech = Double.valueOf(echelle);
		try {
			// for all entities
			while (iteratorCity.hasNext()) {
				SimpleFeature city = iteratorCity.next();
				double[] repetCells = new double[fileRepli.size()];
				if (cellsByCity.containsKey((String) city.getAttribute("NOM_COM"))) {
					repetCells = cellsByCity.remove((String) city.getAttribute("NOM_COM"));
				}
				int nbRepli = 0;
				for (File f : fileRepli) {
					GridCoverage2D repet = Rasters.importRaster(f, (Geometry) city.getDefaultGeometry());
					if (repet != null) {
						Envelope2D env = repet.getEnvelope2D();
						double Xmin = env.getMinX();
						double Xmax = env.getMaxX();
						double Ymin = env.getMinY();
						double Ymax = env.getMaxY();
						for (double r = Xmin + ech / 2; r <= Xmax; r = r + ech) {
							for (double t = Ymin + ech / 2; t <= Ymax; t = t + ech) {
								DirectPosition2D coordCentre = new DirectPosition2D(r, t);
								float[] cellMup = (float[]) repet.evaluate(coordCentre);
								if (cellMup[0] > 0) {
									if (((Geometry) city.getDefaultGeometry()).covers(factory.createPoint(new Coordinate(coordCentre.getX(), coordCentre.getY())))) {
										if (surf) {
											repetCells[nbRepli] = repetCells[nbRepli] + ech * ech;
										} else {
											repetCells[nbRepli] = repetCells[nbRepli] + 1;
										}
									}

								}
							}
						}
						nbRepli = nbRepli + 1;
					}
				}
				cellsByCity.put((String) city.getAttribute("NOM_COM"), repetCells);
			}

		} catch (Exception problem) {
			problem.printStackTrace();
		} finally {
			iteratorCity.close();
		}

		Hashtable<String, double[]> result = new Hashtable<String, double[]>();

		// calculate the coefficient of variation
		for (String city : cellsByCity.keySet()) {
			double[] line = new double[fileRepli.size() + 1];
			DescriptiveStatistics coeff = new DescriptiveStatistics();
			int number = 1;
			for (double val : cellsByCity.get(city)) {
				coeff.addValue(val);
				line[number] = val;
				number++;
			}
			line[0] = coeff.getStandardDeviation() / coeff.getMean();
			result.put(city, line);
		}

		Csv.generateCsvFile(result, statFile, "cellsForCities", nameLineFabric, false);
		fabricSDS.dispose();

		return statFile;
	}

	/**
	 * historique du nombre de cellules sélectionné par scénarios
	 * 
	 * @return the .csv file
	 * @throws IOException
	 */
	public static void createStatsEvol(double[] histo, String echelle) throws IOException {

		Hashtable<String, double[]> enForme = new Hashtable<String, double[]>();
		enForme.put("histo", histo);
		Csv.generateCsvFileCol(enForme, statFile, "selected_cells_all_simu-" + echelle);
	}

	/**
	 * Put all evaluations of an input in a .csv file
	 *
	 */
	public static void createStatEvals(Hashtable<DirectPosition2D, Float> cellEvalFinal) throws Exception {

		// fichier final
		Hashtable<String, double[]> deuForme = new Hashtable<String, double[]>();
		double[] distrib = new double[cellEvalFinal.size()];
		int cpt = 0;
		for (DirectPosition2D it : cellEvalFinal.keySet()) {
			distrib[cpt] = cellEvalFinal.get(it);
			cpt++;
		}
		deuForme.put("Évaluations du scénario", distrib);
		Csv.generateCsvFileCol(deuForme, statFile, "evaluation_moyenne-" + echelle);
	}

	private static Hashtable<DirectPosition2D, Float> moyenneEvals(Hashtable<DirectPosition2D, ArrayList<Float>> cellEval) {
		Hashtable<DirectPosition2D, Float> cellEvalFinal = new Hashtable<DirectPosition2D, Float>();
		for (DirectPosition2D temp : cellEval.keySet()) {
			float somme = 0;
			ArrayList<Float> tablTemp = new ArrayList<Float>();
			tablTemp.addAll(cellEval.get(temp));
			for (float nombre : tablTemp) {
				somme = somme + nombre;
			}
			cellEvalFinal.put(temp, somme / tablTemp.size());
		}
		return cellEvalFinal;
	}

	public static void HighAndLowEvals(Hashtable<DirectPosition2D, ArrayList<Float>> cellEval, File fileOut, File fileExample) throws IOException {
		Hashtable<DirectPosition2D, Float> cellEvalMin = new Hashtable<DirectPosition2D, Float>();
		Hashtable<DirectPosition2D, Float> cellEvalMax = new Hashtable<DirectPosition2D, Float>();
		for (DirectPosition2D temp : cellEval.keySet()) {
			ArrayList<Float> tablTemp = new ArrayList<Float>();
			tablTemp.addAll(cellEval.get(temp));
			float valMin = 1;
			float valMax = 0;

			for (float nombre : tablTemp) {

				if (nombre > valMax) {
					valMax = nombre;
					cellEvalMax.put(temp, valMax);
				} else if (nombre < valMin) {
					valMin = nombre;
					cellEvalMin.put(temp, valMin);
				}
			}
		}

		RasterMerge.writeGeotiff(cellEvalMin, 20, new File(fileOut, "rastMin.tif"), fileExample);
		RasterMerge.writeGeotiff(cellEvalMax, 20, new File(fileOut, "rastMax.tif"), fileExample);
	}

	public static File createStatsDescriptive(String nameScenar, RasterMergeResult result) throws IOException {
		return createStatsDescriptive(nameScenar, result, result.getNbScenar(), false);
	}

	public static File createStatsDescriptive(String nameTest, RasterMergeResult mergedResult, boolean surface) throws IOException {
		return createStatsDescriptive(nameTest, mergedResult, mergedResult.getNbScenar(), surface);
	}

	/**
	 * 
	 * @param nameScenar
	 * @param result
	 * @param variationThreshold
	 *            : if we want to consider cells as variable or stable with other repetition values than maximum or minimum
	 * @return
	 * @throws IOException
	 */
	public static File createStatsDescriptive(String nameScenar, RasterMergeResult result, double variationThreshold, boolean surface) throws IOException {

		statFile.mkdirs();

		StatTab tableauStat = statDescriptive(nameScenar, result, variationThreshold, surface);

		tableauStat.toCsv(statFile, firstline);

		return statFile;
	}

	/**
	 * get a spectial characeristic from a set of mupCity's output
	 * 
	 * @param statab
	 *            :
	 * @param simuCode
	 * @return
	 */

	public static StatTab statDescriptive(String nameScenar, RasterMergeResult result, double variationThreshold, boolean surface) {
		Hashtable<DirectPosition2D, Integer> cellRepet = result.getCellRepet();
		Hashtable<DirectPosition2D, Float> cellEval = result.getCellEval();
		DescriptiveStatistics statNb = result.getHistoDS();

		double[] tableauFinal = new double[(int) (12 + result.getNbScenar())];
		String[] premiereCol = new String[(int) (12 + result.getNbScenar())];

		if (surface) {
			tableauFinal = new double[(int) (13 + result.getNbScenar())];
			premiereCol = new String[(int) (13 + result.getNbScenar())];
		}

		DescriptiveStatistics statInstable = new DescriptiveStatistics();
		DescriptiveStatistics statStable = new DescriptiveStatistics();

		// des statistiques du merge des rasters

		// statistiques du nombre de cellules par scénario
		tableauFinal[0] = Double.parseDouble(echelle);
		premiereCol[0] = "echelle";
		tableauFinal[1] = statNb.getMean();
		premiereCol[1] = "nombre moyen de cellules selectionnees par simulations";
		tableauFinal[2] = statNb.getStandardDeviation();
		premiereCol[2] = "ecart-type du nombre des cellules sélectionnées par simulations";
		tableauFinal[3] = tableauFinal[2] / tableauFinal[1];
		premiereCol[3] = "coeff de variation du nombre de cellules selectionnees par simulations";

		// tableaux servant à calculer les coefficients de correlations
		double[] tableauMoy = new double[cellEval.size()];
		double[] tableauRepl = new double[cellRepet.size()];

		// pour les réplis
		int j = 0;
		int i = 0;
		for (int repli : cellRepet.values()) {
			tableauRepl[i] = repli;
			i = i + 1;
		}

		// pour les evals
		for (float eval : cellEval.values()) {
			tableauMoy[j] = eval;
			j = j + 1;
		}
		// calcul de la correlation entre les réplis et les évals
		if (tableauMoy.length > 1 && stabilite == false) { // si il n'y a pas de cellules, la covariance fait planter
			double correlationCoefficient = new Covariance().covariance(tableauMoy, tableauRepl);
			tableauFinal[4] = correlationCoefficient;
			premiereCol[4] = ("coefficient de correlation entre le nombre de replication et les evaluations des cellules");
		} else {
			if (tableauMoy.length > 1) {
				double correlationCoefficient = new Covariance().covariance(tableauMoy, tableauRepl);
				tableauFinal[5] = correlationCoefficient;
				premiereCol[5] = ("coefficient de correlation entre le nombre de replication et les evaluations des cellules");
			} else {
				tableauFinal[5] = 99;
			}
		}

		int index = 11;
		if (surface) {
			index = 12;
		}

		for (int ii = 1; ii <= result.getNbScenar(); ii++) {
			premiereCol[ii + index] = ("repet " + ii);
			for (DirectPosition2D key : cellRepet.keySet()) {
				if (cellRepet.get(key) == ii) {
					tableauFinal[ii + index]++;
					if (ii < variationThreshold) {
						statInstable.addValue(cellEval.get(key));
					} else {
						statStable.addValue(cellEval.get(key));
					}
				}
			}
		}

		premiereCol[6] = ("moyenne evaluation des cellules instables (strictement inférieures à " + variationThreshold + " réplications)");
		premiereCol[7] = ("ecart type des cellules instables");
		premiereCol[8] = ("coefficient de variation des cellules instables");
		premiereCol[9] = ("moyenne evaluation des cellules stables");
		premiereCol[10] = ("ecart type des cellules stables");
		premiereCol[11] = ("coefficient de variation des cellules stables");
		if (surface) {
			premiereCol[12] = ("surface moyenne occupé par les cellules");
		}

		tableauFinal[6] = statInstable.getMean();
		tableauFinal[7] = statInstable.getStandardDeviation();
		tableauFinal[8] = tableauFinal[6] / tableauFinal[7];
		tableauFinal[9] = statStable.getMean();
		tableauFinal[10] = statStable.getStandardDeviation();
		tableauFinal[11] = tableauFinal[10] / tableauFinal[9];
		if (surface) {
			tableauFinal[12] = tableauFinal[1] * tableauFinal[0] * tableauFinal[0];
		}
		return new StatTab("descriptive_statistics", nameScenar, tableauFinal, premiereCol);
	}

}
