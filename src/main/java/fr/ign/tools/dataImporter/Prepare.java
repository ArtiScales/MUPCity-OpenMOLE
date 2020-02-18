package fr.ign.tools.dataImporter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.operation.TransformException;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import fr.ign.cogit.GTFunctions.Attribute;
import fr.ign.cogit.GTFunctions.Csv;
import fr.ign.cogit.GTFunctions.Schemas;
import fr.ign.cogit.GTFunctions.Vectors;

public class Prepare {

	static boolean multipleDepartment;
	static File rootFolder, folderIn, folderOut, tmpFolder, buildingFolder, roadFolder, amenityFolder, adminFile,
			vegeFolder, trainFolder, NUFolder, hydroFolder, empriseFile;
	static String bdTopoVersion = "";
	
	public static List<String> getShpNamesFromBDTopo(String theme, String version) {
		List<String> result = new ArrayList<String>();

		switch (theme) {
		case "building":
			switch (version) {
			case "2012":
				result.add("BATI_INDIFFERENCIE.SHP");
				result.add("BATI_REMARQUABLE.SHP");
				result.add("BATI_INDUSTRIEL.SHP");
				break;
			default:
				result.add("BATIMENT.shp");
				break;
			}
			break;
		case "buildingNU":
			switch (version) {
			case "2012":
				result.add("CIMETIERE.SHP");
				result.add("CONSTRUCTION_LEGERE.SHP");
				result.add("PISTE_AERODROME.SHP");
				result.add("RESERVOIR.SHP");
				result.add("TERRAIN_SPORT.SHP");
				break;
			default:
				result.add("TERRAIN_DE_SPORT.shp");
				result.add("RESERVOIR.shp");
				result.add("CONSTRUCTION_SURFACIQUE.shp");
				result.add("CIMETIERE.shp");
				break;
			}
			break;
		case "hydro":
			switch (version) {
			case "2012":
				result.add("SURFACE_EAU.SHP");
				break;
			default:
				result.add("SURFACE_HYDROGRAPHIQUE.shp");
				result.add("PLAN_D_EAU.shp");
				break;
			}
			break;		
		case "vege": 	
			switch (version) {
			case "2012":
				result.add("SURFACE_EAU.SHP");
				break;
			default:
				result.add("ZONE_DE_VEGETATION.shp");
				break;
			}
		case "train": 	
			switch (version) {
			case "2012":
				result.add("TRONCON_VOIE_FERREE.SHP");
				break;
			default:
				result.add("TRONCON_DE_VOIE_FERREE.shp");
				break;
			}
		case "trainNU": 	
			switch (version) {
			case "2012":
				result.add("AIRE_TRIAGE.SHP");
				break;
			default:
				result.add("AERODROME.shp");
				result.add("PISTE_D_AERODROME.shp");
				result.add("EQUIPEMENT_DE_TRANSPORT.shp");
				break;
			}
		case "road": 	
			switch (version) {
			case "2012":
				result.add("ROUTE_PRIMAIRE.SHP");
				result.add("ROUTE_SECONDAIRE.SHP");
				result.add("TRONCON_ROUTE.SHP");
				break;
			default:
				result.add("TRONCON_DE_ROUTE.shp");
				break;
			}			
		}
		return result;

	}

	/**
	 * Former formalization of bdtopo
	 * 
	 * @throws Exception
	 */
	public static void prepareBuild() throws Exception {
		if (multipleDepartment) {
			Tools.mergeMultipleBdTopo(buildingFolder, empriseFile);
		}
		// Final build file
		List<File> listShpFinal = new ArrayList<>();
		for (String shp : getShpNamesFromBDTopo("building", bdTopoVersion)) {
			listShpFinal.add(new File (buildingFolder, shp));
		}
		Vectors.mergeVectFiles(listShpFinal, new File(folderOut, "building.shp"), empriseFile, true);
		// create the Non-urbanizable shapefile
		for (String shp : getShpNamesFromBDTopo("buildingNU", bdTopoVersion)) {
			listShpFinal.add(new File (buildingFolder, shp));
		}
		Vectors.mergeVectFiles(listShpFinal, new File(folderOut, "NU/artificial.shp"), new File(""), false);
	}

	public static void prepareHydrography() throws Exception {
		if (multipleDepartment) {
			Tools.mergeMultipleBdTopo(hydroFolder, empriseFile);
		}
		List<File> listShpNu = new ArrayList<>();
		for (String shp : getShpNamesFromBDTopo("hydro", bdTopoVersion)) {
			listShpNu.add(new File(hydroFolder, shp));
		}
		Vectors.mergeVectFiles(listShpNu, new File(folderOut, "NU/hydro.shp"), new File(""), false);
	}

	public static void prepareVege() throws Exception {
		if (multipleDepartment) {
			Tools.mergeMultipleBdTopo(vegeFolder, empriseFile);
		}
	}

	public static void prepareTrain() throws Exception {
		if (multipleDepartment) {
			Tools.mergeMultipleBdTopo(trainFolder, empriseFile);
		}
		// create the Non-urbanizable shapefile
		SimpleFeatureBuilder sfBuilder = Schemas.getBasicSchema("trainBuffer");
		DefaultFeatureCollection bufferTrain = new DefaultFeatureCollection();
			
		for (String shp : getShpNamesFromBDTopo("train", bdTopoVersion)) {
			ShapefileDataStore trainSDS = new ShapefileDataStore((new File(trainFolder, shp)).toURI().toURL());
			SimpleFeatureCollection trainSFC = trainSDS.getFeatureSource().getFeatures();
			Arrays.stream(trainSFC.toArray(new SimpleFeature[0])).forEach(feat -> {
				Geometry featGeom = ((Geometry) feat.getDefaultGeometry()).buffer(7.5);
				if (((String) feat.getAttribute("NATURE")).contains("LGV")) {
					featGeom = ((Geometry) feat.getDefaultGeometry()).buffer(10);
				}
				sfBuilder.add(featGeom);
				bufferTrain.add(sfBuilder.buildFeature(null));
			});
			trainSDS.dispose();
		}
		for (String shp : getShpNamesFromBDTopo("trainNU", bdTopoVersion)) {
		ShapefileDataStore trainTriSDS = new ShapefileDataStore((new File(trainFolder, shp)).toURI().toURL());
			trainTriSDS.setCharset(Charset.forName("UTF-8"));
			SimpleFeatureCollection trainAT_SFC = trainTriSDS.getFeatureSource().getFeatures();
			Arrays.stream(trainAT_SFC.toArray(new SimpleFeature[0])).forEach(feat -> {
				if (shp.equals("EQUIPEMENT_DE_TRANSPORT")
						&& (feat.getAttribute("NATURE").equals("Station de tramway")
								|| feat.getAttribute("NATURE").equals("Port"))
						|| feat.getAttribute("NATURE").equals("Carrefour")
						|| feat.getAttribute("NATURE").equals("Arrêt voyageurs")
						|| feat.getAttribute("NATURE").equals("Aire de repos ou de service")) {
				} else {
					sfBuilder.add(((Geometry) feat.getDefaultGeometry()));
					bufferTrain.add(sfBuilder.buildFeature(null));
				}
			});
		trainTriSDS.dispose();
		}
		Vectors.exportSFC(bufferTrain.collection(), new File(folderOut, "NU/bufferTrain.shp"));
	}

	public static void prepareRoad() throws Exception {
		if (multipleDepartment) {
			Tools.mergeMultipleBdTopo(roadFolder, empriseFile);
		}

		List<File> listShp = new ArrayList<>();
		for (String shp : getShpNamesFromBDTopo("road", bdTopoVersion)) {
			listShp.add(new File(roadFolder, shp));
		}
	
		File roadMerged = Vectors.mergeVectFiles(listShp, new File(tmpFolder, "road.shp"), empriseFile, true);
		File tmpTmpRoadFile = new File(tmpFolder, "roadSpeed.shp");
		File finalRoadFile = new File(folderOut, "road.shp");
		Tools.setSpeed(roadMerged, tmpTmpRoadFile);
		Tools.deleteIsolatedRoadSections(tmpTmpRoadFile, finalRoadFile);

		// create the Non-urbanizable shapefile

		ShapefileDataStore routesSDS = new ShapefileDataStore(roadMerged.toURI().toURL());
		routesSDS.setCharset(Charset.forName("UTF-8"));
		SimpleFeatureCollection routesSFC = routesSDS.getFeatureSource().getFeatures();
		DefaultFeatureCollection bufferRoute = new DefaultFeatureCollection();
		DefaultFeatureCollection bufferRouteExtra = new DefaultFeatureCollection();

		SimpleFeatureBuilder sfBuilder = Schemas.getBasicSchema("routeBuffer");
//		Arrays.stream(routesSFC.toArray(new SimpleFeature[0])).forEach(feat -> {
		SimpleFeatureIterator featIt = routesSFC.features();
		try {
			while (featIt.hasNext()) {
				SimpleFeature feat = featIt.next();
				String largeur = (String) feat.getAttribute("LARGEUR");
				if (largeur == null || largeur.equals("0")) {
					continue;
				}
			System.out.println(feat);

			Geometry newFeat = ((Geometry) feat.getDefaultGeometry()).buffer((Double.parseDouble(largeur)));
			String nature = ((String) feat.getAttribute("NATURE")).toLowerCase();
			System.out.println(newFeat);
			System.out.println(nature);
			if (nature.equals("autoroute")) {
				newFeat = ((Geometry) feat.getDefaultGeometry()).buffer(100);
			}
			if (nature.equals("bretelle") || nature.equals("route à 2 chaussées")
					|| nature.equals("quasi-autoroute")) {
				newFeat = ((Geometry) feat.getDefaultGeometry()).buffer(75);
			}
			sfBuilder.add(newFeat);
			bufferRoute.add(sfBuilder.buildFeature(null));
			}
			} catch (Exception problem) {
				problem.printStackTrace();
			} finally {
				featIt.close();
			}

//		});
		Vectors.exportSFC(bufferRoute.collection(), new File(folderOut, "NU/bufferRoad.shp"));
		Vectors.exportSFC(bufferRouteExtra.collection(), new File(folderOut, "NU/bufferExtraRoad.shp"));
		routesSDS.dispose();
	}

	/**
	 * TODO when i get there, regarder si et comment se sont créées beaucoup de géométries
	 * vides.
	 * 
	 * @throws Exception
	 */
	public static void makeFullZoneNU() throws Exception {
		File rootFileNU = new File(folderOut, "NU");
		List<File> listFullNU = new ArrayList<File>();
		listFullNU.add(new File(rootFileNU, "bufferRoad.shp"));
		listFullNU.add(new File(rootFileNU, "bufferExtraRoad.shp"));
		listFullNU.add(new File(rootFileNU, "hydro.shp"));
		listFullNU.add(new File(rootFileNU, "artificial.shp"));
		listFullNU.add(new File(rootFileNU, "bufferTrain.shp"));

		for (File f : NUFolder.listFiles()) {
			if (f.getName().endsWith(".shp")) {
				listFullNU.add(f);
			}
		}
		File nUUnKut = new File(rootFileNU.getParentFile(), "nonUrbaUncut.shp");
		Vectors.mergeVectFiles(listFullNU, nUUnKut, null, false);
		Vectors.discretizeShp(nUUnKut, new File(rootFileNU.getParentFile(), "nonUrba.shp"), "nonUrba");
	}

	public static void makePhysicNU() throws Exception {
		File rootFileNU = new File(folderOut, "NU");
		List<File> listFullNU = new ArrayList<File>();
		listFullNU.add(new File(rootFileNU, "bufferRoad.shp"));
		listFullNU.add(new File(rootFileNU, "hydro.shp"));
		listFullNU.add(new File(rootFileNU, "artificial.shp"));
		listFullNU.add(new File(rootFileNU, "bufferTrain.shp"));
		File nUUnKut = new File(rootFileNU.getParentFile(), "nonUrbaPhyUncut.shp");
		Vectors.mergeVectFiles(listFullNU, nUUnKut, null, false);
		Vectors.discretizeShp(nUUnKut, new File(rootFileNU.getParentFile(), "nonUrbaPhy.shp"), "nonUrbaPhy");
	}

	public static void setMultipleDepartment(boolean multipleDepartment) {
		Prepare.multipleDepartment = multipleDepartment;
	}

	public static void setRootFolder(File rootFolder) {
		Prepare.rootFolder = rootFolder;
	}

	public static void setFolderIn(File folderIn) {
		Prepare.folderIn = folderIn;
	}

	public static void setFolderOut(File folderOut) {
		Prepare.folderOut = folderOut;
	}

	public static void setTmpFolder(File tmpFolder) {
		Prepare.tmpFolder = tmpFolder;
	}

	public static void setBuildingFolder(File buildingFolder) {
		Prepare.buildingFolder = buildingFolder;
	}

	public static void setRoadFolder(File roadFolder) {
		Prepare.roadFolder = roadFolder;
	}

	public static void setAmenityFolder(File amenityFolder) {
		Prepare.amenityFolder = amenityFolder;
	}

	public static void setAdminFile(File adminFile) {
		Prepare.adminFile = adminFile;
	}

	public static void setVegeFolder(File vegeFolder) {
		Prepare.vegeFolder = vegeFolder;
	}

	public static void setTrainFolder(File trainFolder) {
		Prepare.trainFolder = trainFolder;
	}

	public static void setNUFolder(File nUFolder) {
		NUFolder = nUFolder;
	}

	public static void setHydroFolder(File hydroFolder) {
		Prepare.hydroFolder = hydroFolder;
	}

	public static void setEmpriseFile(File empriseFile) throws MalformedURLException, NoSuchAuthorityCodeException, IOException, ParseException, FactoryException {
		Prepare.empriseFile = empriseFile;
		if (!empriseFile.exists()) {
			Tools.createEmpriseFile(folderIn, adminFile);
		}
	}

	/**
	 * trie les aménités déjà géocodés de SIRENE - contenue dans un fichier
	 * dataIn/sireneBPE/sirene.shp et de BPE contenue dans le fichier
	 * dataIn/sireneBPE/BPE-tot.csv
	 * 
	 * @param rootFile    : dossier principal
	 * @param empriseFile : shp de l'emprise générale (auto-généré)
	 * @param nbDep       : liste des départements à prendre en compte
	 * @throws Exception
	 */
	public static void sortAmenities() throws Exception {
		File sireneFile = new File(amenityFolder, "sirene.csv");
		if (multipleDepartment) {
			sireneFile = new File(tmpFolder, "sirene.csv");
			Csv.mergeCSVFiles(new File(amenityFolder, "sirene"), sireneFile);
		}
		CSVReader sirene = new CSVReader(new FileReader(sireneFile));
		String[] header = sirene.readNext();
		sirene.close();
		sortSirene(header, sireneFile);

//		sortBPE();
//		mergeAmenities();
	}

	/**
	 * regular indices from SIRENE ddb (may change?!) TODO replace indices
	 * 
	 * @throws IOException
	 * @throws MismatchedDimensionException
	 * @throws NoSuchAuthorityCodeException
	 * @throws FactoryException
	 * @throws ParseException
	 * @throws TransformException
	 */
	public static void sortSirene(String[] sireneHeader, File sireneFile)
			throws IOException, MismatchedDimensionException, NoSuchAuthorityCodeException, FactoryException,
			ParseException, TransformException {
		sortSirene(sireneHeader, sireneFile, "separateField", Attribute.getLatIndice(sireneHeader),
				Attribute.getLongIndice(sireneHeader), 0, 0);
	}

	/**
	 * TODO verify if it goes well (attribute numbers must change)
	 * 
	 * @throws IOException
	 * @throws MismatchedDimensionException
	 * @throws NoSuchAuthorityCodeException
	 * @throws FactoryException
	 * @throws ParseException
	 * @throws TransformException
	 */
	public static void sortSirene(String[] sireneHeader, File sireneFile, String coordType, int iPos1, int iPos2,
			int iTypeGen, int iTypePart) throws IOException, MismatchedDimensionException, NoSuchAuthorityCodeException,
			FactoryException, ParseException, TransformException {
		// if multiple points

		File serviceSirene = new File(tmpFolder, "sireneServices.csv");
		File loisirSirene = new File(tmpFolder, "sireneLoisirs.csv");
		if (serviceSirene.exists()) {
			Files.delete(serviceSirene.toPath());
		}
		if (serviceSirene.exists()) {
			Files.delete(serviceSirene.toPath());
		}
		preselecGeocode(sireneFile, adminFile);

		CSVReader csvSIRENE = new CSVReader(new FileReader(sireneFile));
		CSVWriter csvServiceSirene = new CSVWriter(new FileWriter(serviceSirene, true));
		CSVWriter csvLoisirSirene = new CSVWriter(new FileWriter(loisirSirene, true));
		String[] firstLineSirene = csvSIRENE.readNext();
		String[] newFirstLineSirene = new String[firstLineSirene.length + 2];
		for (int k = 0; k < firstLineSirene.length; k = k + 1) {
			newFirstLineSirene[k] = firstLineSirene[k];
		}
		newFirstLineSirene[firstLineSirene.length] = "TYPE";
		newFirstLineSirene[firstLineSirene.length + 1] = "LEVEL";
		csvLoisirSirene.writeNext(newFirstLineSirene);
		csvServiceSirene.writeNext(newFirstLineSirene);

		ShapefileDataStore envSDS = new ShapefileDataStore(empriseFile.toURI().toURL());
		ReferencedEnvelope env = (envSDS.getFeatureSource().getFeatures()).getBounds();
		for (String[] row : csvSIRENE.readAll()) {
			String[] result = new String[firstLineSirene.length + 2];
			if (!(row[iTypeGen].isEmpty())) {
				Double[] coord = Tools.getCoordFromCSV(coordType, row[iPos1], row[iPos2]);
				if (coord == null) {
					continue;
				}
				double x = coord[0];
				double y = coord[1];
				if (x < env.getMaxX() && x > env.getMinX() && y < env.getMaxY() && y > env.getMinY()) {
					String[] resultOut = SortAmenitiesCategories.sortCategoriesAmenenitiesNAFCPF(row[iTypeGen]);
					if (!(resultOut[0] == null)) {
						for (int i = 0; i < 9; i = i + 1) {
							result[i] = row[i];
						}
						result[firstLineSirene.length] = resultOut[1];
						result[firstLineSirene.length + 1] = resultOut[2];
						switch (resultOut[0]) {
						case "service":
							csvServiceSirene.writeNext(result);
							break;
						case "loisir":
							csvLoisirSirene.writeNext(result);
							break;
						}
					}
				}
			}
		}
		envSDS.dispose();
		csvSIRENE.close();
		csvServiceSirene.close();
		csvLoisirSirene.close();

		Tools.createPointFromCsv(serviceSirene, new File(rootFolder, "tmp/SIRENE-Services.shp"), empriseFile,
				"service");
		Tools.createPointFromCsv(loisirSirene, new File(rootFolder, "tmp/SIRENE-Loisirs.shp"), empriseFile, "leisure");
	}

	/**
	 * Classe servant à trier les entrées d'un CSV Sirene en comparant leurs codes
	 * postaux à une liste de villes
	 * 
	 * @param pointIn    : le CSV contenant les aménités à trier
	 * @param pointVille : la liste des villes
	 * @return
	 * @throws IOException
	 */
	public static File preselecGeocode(File pointIn, File pointVille) throws IOException {
		File pointOut = new File(tmpFolder, "sireneTri.csv");

		// no double file
		if (pointOut.exists()) {
			Files.delete(pointOut.toPath());
		}
		CSVReader csvVille = new CSVReader(new FileReader(pointVille));
		CSVReader csvAm = new CSVReader(new FileReader(pointIn));
		List<String[]> listVille = csvVille.readAll();
		List<String[]> listAm = csvAm.readAll();

		// get zipcodes indices
		int numCodPostSiren = Attribute.getINSEEIndice(listAm.get(0));
		int numCodPost = Attribute.getINSEEIndice(listVille.get(0));

		// collection pour éliminer les doublons
		ArrayList<String> deleteDouble = new ArrayList<>();

		// copie des points sélectionnées dans un nouveau csv
		CSVWriter csv2copy = new CSVWriter(new FileWriter(pointOut, false));
		csv2copy.writeNext((String[]) listAm.get(0));
		for (String[] row : listVille) {
			String codePost = row[numCodPost];
			if (!deleteDouble.contains(codePost)) {
				for (String[] rOw : listAm) {
					if (codePost.toUpperCase().equals(rOw[numCodPostSiren].toUpperCase())) {
						csv2copy.writeNext(rOw);
					}
				}
			}
			deleteDouble.add(codePost);
		}
		csv2copy.close();
		csvVille.close();
		csvAm.close();
		Files.copy(pointOut.toPath(), pointIn.toPath(), new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
		Files.delete(pointOut.toPath());
		return pointOut;
	}

	public static void sortBPE() throws IOException, MismatchedDimensionException, NoSuchAuthorityCodeException,
			FactoryException, ParseException, TransformException {
		// for the BPE file
		File pointBPEIn = new File(rootFolder, "dataIn/sireneBPE/BPE-tot.csv");
		File csvServicesBPE = new File(rootFolder, "tmp/BPE-Services.csv");
		File csvLoisirsBPE = new File(rootFolder, "tmp/BPE-Loisirs.csv");
		File csvTrainsBPE = new File(rootFolder, "tmp/BPE-Trains.csv");

		if (csvLoisirsBPE.exists()) {
			Files.delete(csvLoisirsBPE.toPath());
		}
		if (csvServicesBPE.exists()) {
			Files.delete(csvServicesBPE.toPath());
		}
		if (csvTrainsBPE.exists()) {
			Files.delete(csvTrainsBPE.toPath());
		}

		CSVReader csvBPE = new CSVReader(new FileReader(pointBPEIn));
		CSVWriter csvServiceBPE = new CSVWriter(new FileWriter(csvServicesBPE, true));
		CSVWriter csvLoisirBPE = new CSVWriter(new FileWriter(csvLoisirsBPE, true));
		CSVWriter csvTrainBPE = new CSVWriter(new FileWriter(csvTrainsBPE, true));
		String[] firstLineBPE = csvBPE.readNext();
		String[] newFirstLineBPE = new String[firstLineBPE.length + 2];
		for (int k = 0; k < firstLineBPE.length; k = k + 1) {
			newFirstLineBPE[k] = firstLineBPE[k];
		}
		newFirstLineBPE[firstLineBPE.length] = "TYPE";
		newFirstLineBPE[firstLineBPE.length + 1] = "LEVEL";
		csvLoisirBPE.writeNext(newFirstLineBPE);
		csvServiceBPE.writeNext(newFirstLineBPE);
		String[] trainStr = { "NATURE", "X", "Y" };
		csvTrainBPE.writeNext(trainStr);
		ShapefileDataStore envSDS = new ShapefileDataStore(empriseFile.toURI().toURL());
		ReferencedEnvelope env = (envSDS.getFeatureSource().getFeatures()).getBounds();

		for (String[] row : csvBPE.readAll()) {
			String[] result = new String[11];
			if (!(row[6].isEmpty())) {
				Double x = Double.parseDouble((row[6].split(";"))[0]);
				Double y = Double.parseDouble((row[7].split(";"))[0]);
				if (x < env.getMaxX() && x > env.getMinX() && y < env.getMaxY() && y > env.getMinY()) {
					String[] resultOut = SortAmenitiesCategories.sortCategoriesAmenenitiesNAFCPF(row[5]);
					if (!(resultOut[0] == null)) {
						for (int i = 0; i < 9; i = i + 1) {
							result[i] = row[i];
						}
						result[9] = resultOut[1];
						result[10] = resultOut[2];
						String[] resTrain = { result[1], String.valueOf(x), String.valueOf(y) };

						switch (resultOut[0]) {
						case "service":
							csvServiceBPE.writeNext(result);
							break;
						case "loisir":
							csvLoisirBPE.writeNext(result);
							break;
						case "train":
							csvTrainBPE.writeNext(resTrain);
						}
					}
				}
			}
		}
		envSDS.dispose();
		csvBPE.close();
		csvServiceBPE.close();
		csvLoisirBPE.close();
		csvTrainBPE.close();

		Tools.createPointFromCsv(csvServicesBPE, new File(rootFolder, "tmp/BPE-Services.shp"), empriseFile, "service");
		Tools.createPointFromCsv(csvLoisirsBPE, new File(rootFolder, "tmp/BPE-Loisirs.shp"), empriseFile, "leisure");
		Tools.createPointFromCsv(csvTrainsBPE, new File(rootFolder, "dataOut/trainAutom.shp"), empriseFile, "train");
	}

	public static void mergeAmenities() throws Exception {

		// merge multiple services sources
		System.out.println("merge services");
		File pointServices = new File(rootFolder, "dataOut/serviceAutom.shp");
		List<File> listServices = new ArrayList<>();
		File sireneGeocodedServiceFile = new File(rootFolder, "tmp/sirene-service-geocoded.csv");
		if (sireneGeocodedServiceFile.exists()) {
			listServices.add(Tools.createPointFromCsv(sireneGeocodedServiceFile,
					new File(rootFolder, "tmp/Sirene-Services.shp"), empriseFile, "service"));
		} else {
			listServices.add(new File(rootFolder, "tmp/siren-Services.shp"));
		}
		listServices.add(new File(rootFolder, "tmp/BPE-Services.shp"));

		Vectors.mergeVectFiles(listServices, pointServices, empriseFile, true);

		// merge multiple loisirs sources
		System.out.println("merge leisure");
		File pointLoisirs = new File(rootFolder, "dataOut/loisirAutom.shp");
		List<File> listLoisirs = new ArrayList<>();
		listLoisirs.add(new File(rootFolder, "tmp/BPE-Loisirs.shp"));
		File sireneGeocodedLoisirsFile = new File(rootFolder, "tmp/sirene-loisir-geocoded.csv");
		if (sireneGeocodedLoisirsFile.exists()) {
			listLoisirs.add(Tools.createPointFromCsv(sireneGeocodedLoisirsFile,
					new File(rootFolder, "tmp/Sirene-Loisirs.shp"), empriseFile, "leisure"));
		} else {
			listServices.add(new File(rootFolder, "tmp/siren-Services.shp"));
		}
		listLoisirs.add(Tools.createLeisureAccess(new File(rootFolder, "dataIn/vege/ZONE_VEGETATION.SHP"),
				new File(rootFolder, "dataIn/route/CHEMIN.SHP"), new File(rootFolder, "dataOut/routeAutom.shp")));
		Vectors.mergeVectFiles(listLoisirs, pointLoisirs, empriseFile, true);
	}

}
