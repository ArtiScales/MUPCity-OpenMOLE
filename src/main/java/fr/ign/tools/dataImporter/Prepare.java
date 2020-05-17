package fr.ign.tools.dataImporter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import fr.ign.cogit.geoToolsFunctions.Attribute;
import fr.ign.cogit.geoToolsFunctions.Csv;
import fr.ign.cogit.geoToolsFunctions.Schemas;
import fr.ign.cogit.geoToolsFunctions.vectors.Collec;
import fr.ign.cogit.geoToolsFunctions.vectors.Shp;

public class Prepare {

	static boolean multipleDepartment;
	static File rootFolder, folderIn, folderOut, tmpFolder, buildingFolder, transportFolder, amenityFolder, adminFile,
			vegeFolder, NUInFolder, NUOutFolder, hydroFolder, empriseFile;
	static String bdTopoVersion = "";
	
	/**
	 * Get the usual names of the shapefiles depending of the BDTopo version
	 * @param theme Which BDTopo theme is wanted. Must be one of these <ul> <li><i>'building'</i></li>,<li><i>'buildingNU'</i>
	 * 
	 * ,<li><i>'hydro'</i><li>,<li><i>''</i><li>,<li><i>''</i><li> </ul>
	 * @param version
	 * @return
	 */
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
				result.add("AIRE_TRIAGE.SHP");
				break;
			default:
				result.add("TRONCON_DE_VOIE_FERREE.shp");
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
	 * @throws IOException 
	 * 
	 */
	public static void prepareBuild() throws IOException {
		if (multipleDepartment) {
			Tools.mergeMultipleBdTopo(buildingFolder, empriseFile);
		}
		// Final build file
		List<File> listShpFinal = new ArrayList<>();
		for (String shp : getShpNamesFromBDTopo("building", bdTopoVersion))
			listShpFinal.add(new File(buildingFolder, shp));
		Shp.mergeVectFiles(listShpFinal, new File(folderOut, "building.shp"), empriseFile, true);
		// create the Non-urbanizable shapefile
		for (String shp : getShpNamesFromBDTopo("buildingNU", bdTopoVersion))
			listShpFinal.add(new File(buildingFolder, shp));
		Shp.mergeVectFiles(listShpFinal, new File(NUOutFolder, "artificial.shp"), null, false);
	}

	public static void prepareHydrography() throws IOException {
		if (multipleDepartment) {
			Tools.mergeMultipleBdTopo(hydroFolder, empriseFile);
		}
		List<File> listShpNu = new ArrayList<>();
		for (String shp : getShpNamesFromBDTopo("hydro", bdTopoVersion)) {
			listShpNu.add(new File(hydroFolder, shp));
		}
		Shp.mergeVectFiles(listShpNu, new File(NUOutFolder, "hydro.shp"), new File(""), false);
	}

	public static void prepareVege() throws IOException {
		if (multipleDepartment)
			Tools.mergeMultipleBdTopo(vegeFolder, empriseFile);
	}

	public static void prepareTrain() throws NoSuchAuthorityCodeException, FactoryException, IOException {
		if (multipleDepartment)
			Tools.mergeMultipleBdTopo(transportFolder, empriseFile);
		// create the train buffer
		SimpleFeatureBuilder sfBuilder = Schemas.getBasicSchema("trainBuffer");
		DefaultFeatureCollection bufferTrain = new DefaultFeatureCollection();
		for (String shp : getShpNamesFromBDTopo("train", bdTopoVersion)) {
			ShapefileDataStore trainSDS = new ShapefileDataStore((new File(transportFolder, shp)).toURI().toURL());
			trainSDS.setCharset(Charset.forName("UTF-8"));
			try (SimpleFeatureIterator featIt = trainSDS.getFeatureSource().getFeatures().features()) {
				while (featIt.hasNext()) {
					SimpleFeature feat = featIt.next();
					Geometry featGeom = ((Geometry) feat.getDefaultGeometry()).buffer(7.5);
					String nature = (String) feat.getAttribute("NATURE");
					if (nature.contains("LGV"))
						featGeom = ((Geometry) feat.getDefaultGeometry()).buffer(10);
					else if (shp.equals("EQUIPEMENT_DE_TRANSPORT.shp") && (nature.equals("Station de tramway") || nature.equals("Port"))
							|| nature.equals("Carrefour") || nature.equals("Arrêt voyageurs") || nature.equals("Aire de repos ou de service"))
						continue;
					sfBuilder.add(featGeom);
					bufferTrain.add(sfBuilder.buildFeature(Attribute.makeUniqueId()));
				}
			} catch (Exception problem) {
				problem.printStackTrace();
			}
			trainSDS.dispose();
		}
		Collec.exportSFC(bufferTrain.collection(), new File(NUOutFolder, "bufferTrain.shp"));
	}

	public static void prepareRoad() throws IOException, NoSuchAuthorityCodeException, FactoryException {
		//road shapefiles merge, sort and clean isolated segments
		if (multipleDepartment) 
			Tools.mergeMultipleBdTopo(transportFolder, empriseFile);
		List<File> listShp = getShpNamesFromBDTopo("road", bdTopoVersion).stream().map(s -> new File(transportFolder, s)).collect(Collectors.toList());
		File roadMerged = Shp.mergeVectFiles(listShp, new File(tmpFolder, "road.shp"), empriseFile, true);
		File tmpTmpRoadFile = new File(tmpFolder, "roadSpeed.shp");
		File finalRoadFile = new File(folderOut, "road.shp");
		Tools.setSpeed(roadMerged, tmpTmpRoadFile);
		Tools.deleteIsolatedRoadSections(tmpTmpRoadFile, finalRoadFile);
		// create the Non-urbanizable shapefile
		ShapefileDataStore routesSDS = new ShapefileDataStore(roadMerged.toURI().toURL());
		routesSDS.setCharset(Charset.forName("UTF-8"));
		DefaultFeatureCollection bufferRoute = new DefaultFeatureCollection();
		DefaultFeatureCollection bufferRouteExtra = new DefaultFeatureCollection();
		SimpleFeatureBuilder sfBuilder = Schemas.getBasicSchema("routeBuffer");
		try (SimpleFeatureIterator featIt = routesSDS.getFeatureSource().getFeatures().features()) {
			while (featIt.hasNext()) {
				SimpleFeature feat = featIt.next();
				String largeur = String.valueOf(feat.getAttribute("LARGEUR"));
				if (largeur == null || largeur.equals("0") || largeur.equals("") || largeur.equals("null"))
					continue;
				sfBuilder.add(((Geometry) feat.getDefaultGeometry()).buffer((Double.parseDouble(largeur))));
				bufferRoute.add(sfBuilder.buildFeature(Attribute.makeUniqueId()));
				String nature = ((String) feat.getAttribute("NATURE")).toLowerCase();
				double buffer = 0.0;
				if (nature.equals("autoroute") || nature.equals("type autoroutier"))
					buffer = 100.0;
				else if (nature.equals("bretelle") || nature.equals("route à 2 chaussées") || nature.equals("quasi-autoroute"))
					buffer =75.0;
				if (buffer != 0.0) {
					sfBuilder.add(((Geometry) feat.getDefaultGeometry()).buffer(buffer));
					bufferRouteExtra.add(sfBuilder.buildFeature(Attribute.makeUniqueId()));
				}
			}
		} catch (Exception problem) {
			problem.printStackTrace();
		}
		Collec.exportSFC(Collec.gridDiscretize(bufferRoute, DataImporter.getMeshresolution()), new File(NUOutFolder, "bufferRoad.shp"));
		Collec.exportSFC(Collec.gridDiscretize(bufferRouteExtra, DataImporter.getMeshresolution()), new File(NUOutFolder, "bufferExtraRoad.shp"));
		routesSDS.dispose();
	}

	/**
	 * TODO when i get there, regarder si et comment se sont créées beaucoup de géométries
	 * vides.
	 * @throws FactoryException 
	 * @throws IOException 
	 * @throws NoSuchAuthorityCodeException 
	 * 
	 * @throws Exception
	 */
	public static void makeFullZoneNU() throws NoSuchAuthorityCodeException, IOException, FactoryException  {
		List<File> listFullNU = new ArrayList<File>();
		listFullNU.add(new File(NUOutFolder, "bufferRoad.shp"));
		listFullNU.add(new File(NUOutFolder, "bufferExtraRoad.shp"));
		listFullNU.add(new File(NUOutFolder, "hydro.shp"));
		listFullNU.add(new File(NUOutFolder, "artificial.shp"));
		listFullNU.add(new File(NUOutFolder, "bufferTrain.shp"));
		for (File f : NUInFolder.listFiles())
			if (f.getName().endsWith(".shp"))
				listFullNU.add(f);
		File nUUnKut = new File(folderOut, "nonUrbaUncut.shp");
		Shp.mergeVectFiles(listFullNU, nUUnKut, null, false);
		Shp.gridDiscretizeShp(nUUnKut, new File(folderOut, "nonUrba.shp"), DataImporter.getMeshresolution());
	}

	public static void makePhysicNU() throws NoSuchAuthorityCodeException, IOException, FactoryException  {
		List<File> listFullNU = new ArrayList<File>();
		listFullNU.add(new File(NUOutFolder, "bufferRoad.shp"));
		listFullNU.add(new File(NUOutFolder, "hydro.shp"));
		listFullNU.add(new File(NUOutFolder, "artificial.shp"));
		listFullNU.add(new File(NUOutFolder, "bufferTrain.shp"));
		File nUUnKut = new File(folderOut, "nonUrbaPhyUncut.shp");
		Shp.mergeVectFiles(listFullNU, nUUnKut, null, false);
		Shp.gridDiscretizeShp(nUUnKut, new File(folderOut, "nonUrbaPhy.shp"), DataImporter.getMeshresolution());
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

	public static void setTransportFolder(File transportFolder) {
		Prepare.transportFolder = transportFolder;
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

	public static void setNUInFolder(File nUInFolder) {
		NUInFolder = nUInFolder;
	}
	
	public static void setNUOutFolder(File nUOutFolder) {
		NUOutFolder = nUOutFolder;
	}

	public static void setHydroFolder(File hydroFolder) {
		Prepare.hydroFolder = hydroFolder;
	}

	public static void setEmpriseFile(File empriseFile) throws NoSuchAuthorityCodeException, IOException, FactoryException {
		Prepare.empriseFile = empriseFile;
		if (!empriseFile.exists()) 
			Tools.createEmpriseFile(folderIn, adminFile);
	}

	/**
	 * Trie les aménités déjà géocodés de SIRENE - contenue dans un fichier
	 * dataIn/sireneBPE/sirene.shp et de BPE contenue dans le fichier
	 * dataIn/sireneBPE/BPE-tot.csv
	 * 
	 * @param rootFile    dossier principal
	 * @param empriseFile shp de l'emprise générale (auto-généré)
	 * @param nbDep       liste des départements à prendre en compte
	 * @throws IOException 
	 * @throws TransformException 
	 * @throws ParseException 
	 * @throws FactoryException 
	 * @throws NoSuchAuthorityCodeException 
	 * @throws MismatchedDimensionException 
	 * @throws Exception
	 */
	public static void sortAmenities() throws IOException, MismatchedDimensionException, NoSuchAuthorityCodeException, FactoryException, ParseException, TransformException  {
		File sireneFile = new File(amenityFolder, "sirene.csv");
		File BPEFile = new File(amenityFolder, "BPE.csv");

		if (multipleDepartment) {
			Csv.mergeCSVFiles(new File(amenityFolder, "sirene"), sireneFile);
			Csv.mergeCSVFiles(new File(amenityFolder, "BPE"), BPEFile);
		}
		
		CSVReader sirene = new CSVReader(new FileReader(sireneFile));
		String[] header = sirene.readNext();
		sirene.close();
		sortSirene(header, sireneFile);
		CSVReader bpe = new CSVReader(new FileReader(BPEFile));
		String[] headerBPE = bpe.readNext();
		bpe.close();
		sortBPE(headerBPE, BPEFile);
		mergeAmenities();
	}

	/**
	 * regular indices from SIRENE ddb (may change?!) 
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
				Attribute.getLongIndice(sireneHeader), Attribute.getIndice(sireneHeader, DataImporter.getSireneType()));
	}

	/**
	 * 
	 * @param sireneHeader
	 * @param sireneFile
	 * @param coordType
	 * @param iX
	 * @param iY
	 * @param iTypeGen
	 * @throws IOException
	 * @throws MismatchedDimensionException
	 * @throws NumberFormatException
	 * @throws NoSuchAuthorityCodeException
	 * @throws FactoryException
	 * @throws TransformException
	 * @throws ParseException
	 */
	public static void sortSirene(String[] sireneHeader, File sireneFile, String coordType, int iX, int iY, int iTypeGen) throws IOException,
			MismatchedDimensionException, NumberFormatException, NoSuchAuthorityCodeException, FactoryException, TransformException, ParseException {
		File serviceSirene = new File(tmpFolder, "sireneServices.csv");
		File loisirSirene = new File(tmpFolder, "sireneLoisirs.csv");
		File errorAmenities = new File(tmpFolder, "errorAmenities.csv");

		if (serviceSirene.exists()) {
			Files.delete(serviceSirene.toPath());
		}
		if (loisirSirene.exists()) {
			Files.delete(loisirSirene.toPath());
		}
		sireneFile = preparePoint(sireneFile, adminFile, sireneFile, DataImporter.getSireneSRC(), DataImporter.getNameFieldCodeSIRENE());

		CSVReader csvSIRENE = new CSVReader(new FileReader(sireneFile));
		CSVWriter csvServiceSirene = new CSVWriter(new FileWriter(serviceSirene, true));
		CSVWriter csvLoisirSirene = new CSVWriter(new FileWriter(loisirSirene, true));
		CSVWriter csvErrorAmenities = new CSVWriter(new FileWriter(errorAmenities, true));
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
				Double[] coord = Tools.getCoordPointFromCSV(coordType, row[iX], row[iY]);
				if (coord == null) {
					csvErrorAmenities.writeNext(row);
					continue;
				}
				double x = coord[0];
				double y = coord[1];
				if (x < env.getMaxX() && x > env.getMinX() && y < env.getMaxY() && y > env.getMinY()) {
					String[] resultOut = SortAmenitiesCategories.sortCategoriesAmenenitiesNAFCPF(row[iTypeGen]);
					if (!(resultOut[0] == null)) {
						for (int i = 0; i < firstLineSirene.length; i = i + 1) 
							result[i] = row[i];					
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
		csvErrorAmenities.close();
		Tools.createPointFromCsv(serviceSirene, new File(tmpFolder, "SireneService.shp"), empriseFile, "service");
		Tools.createPointFromCsv(loisirSirene, new File(tmpFolder, "SireneLeisure.shp"), empriseFile, "leisure");
	}

	/**
	 * 
	 * @throws IOException
	 * @throws MismatchedDimensionException
	 * @throws NoSuchAuthorityCodeException
	 * @throws FactoryException
	 * @throws ParseException
	 * @throws TransformException
	 */
	public static void sortBPE(String[] BPEHeader, File BPEFile, String coordType, int iPos1, int iPos2, int iTypeGen)
			throws IOException, MismatchedDimensionException, NoSuchAuthorityCodeException, FactoryException, TransformException, ParseException {

		// for the BPE file
		File pointBPEIn = new File(amenityFolder, "BPE.csv");
		File csvServicesBPE = new File(tmpFolder, "BPE-Services.csv");
		File csvLoisirsBPE = new File(tmpFolder, "BPE-Loisirs.csv");
		File csvTrainsBPE = new File(tmpFolder, "BPE-Trains.csv");
		File errorAmenities = new File(tmpFolder, "errorAmenities.csv");

		if (csvLoisirsBPE.exists()) {
			Files.delete(csvLoisirsBPE.toPath());
		}
		if (csvServicesBPE.exists()) {
			Files.delete(csvServicesBPE.toPath());
		}
		if (csvTrainsBPE.exists()) {
			Files.delete(csvTrainsBPE.toPath());
		}

		BPEFile = preparePoint(BPEFile, adminFile, BPEFile, DataImporter.getBpeSRC(), DataImporter.getNameFieldCodeBPE());

		CSVReader csvBPE = new CSVReader(new FileReader(pointBPEIn));
		CSVWriter csvServiceBPE = new CSVWriter(new FileWriter(csvServicesBPE, true));
		CSVWriter csvLoisirBPE = new CSVWriter(new FileWriter(csvLoisirsBPE, true));
		CSVWriter csvTrainBPE = new CSVWriter(new FileWriter(csvTrainsBPE, true));
		CSVWriter csvErrorAmenities = new CSVWriter(new FileWriter(errorAmenities, true));

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
			String[] result = new String[firstLineBPE.length +2];
			if (!(row[iTypeGen].isEmpty())) {
				Double[] coord = Tools.getCoordPointFromCSV(coordType, row[iPos1], row[iPos2]);
				if (coord == null) {
					csvErrorAmenities.writeNext(row);
					continue;
				}
				double x = coord[0];
				double y = coord[1];
				if (x < env.getMaxX() && x > env.getMinX() && y < env.getMaxY() && y > env.getMinY()) {
					String[] resultOut = SortAmenitiesCategories.sortCategoriesAmenenitiesNAFCPF(row[iTypeGen]);
					if (!(resultOut[0] == null)) {
						for (int i = 0; i < firstLineBPE.length; i = i + 1) 
							result[i] = row[i];
						result[firstLineBPE.length] = resultOut[1];
						result[firstLineBPE.length + 1] = resultOut[2];
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
		csvErrorAmenities.close();
		csvServiceBPE.close();
		csvLoisirBPE.close();
		csvTrainBPE.close();
		Tools.createPointFromCsv(csvServicesBPE, new File(tmpFolder, "BPEService.shp"), empriseFile, "service");
		Tools.createPointFromCsv(csvLoisirsBPE, new File(tmpFolder, "BPELeisure.shp"), empriseFile, "leisure");
		//TODO don't work for now - find better codes 
		//		Tools.createPointFromCsv(csvTrainsBPE, new File(folderOut, "train.shp"), empriseFile, "train");
	}

	/**
	 * Merges the amenities shapefiles previously generated (Sirene and BPE).
	 * @throws MismatchedDimensionException
	 * @throws NoSuchAuthorityCodeException
	 * @throws IOException
	 * @throws FactoryException
	 * @throws TransformException
	 * @throws ParseException
	 */
	public static void mergeAmenities() throws MismatchedDimensionException, NoSuchAuthorityCodeException, IOException, FactoryException, TransformException, ParseException {

		// results
		File pointService = new File(folderOut, "service.shp");
		File pointLeisure = new File(folderOut, "leisure.shp");

		// previously generated shapefiles
		List<File> listServices = new ArrayList<>();
		listServices.add(new File(tmpFolder, "SireneService.shp"));
		listServices.add(new File(tmpFolder, "BPEService.shp"));
		Shp.mergeVectFiles(listServices, pointService, empriseFile, true);

		List<File> listLeisure = new ArrayList<>();
		listLeisure.add(new File(tmpFolder, "BPELeisure.shp"));
		listLeisure.add(new File(tmpFolder, "SireneLeisure.shp"));
		listLeisure.add(
				Tools.createLeisureAccess(new File(vegeFolder, "ZONE_DE_VEGETATION.shp"), new File(transportFolder, "TRONCON_DE_ROUTE.shp"), empriseFile,tmpFolder));
		Shp.mergeVectFiles(listLeisure, pointLeisure, empriseFile, true);
	}

	/**
	 * This method is used to prepare .csv entries of a File. It will sort them in comparing their community codes to an input city list. It also will convert the coordinates to
	 * the designed coordinate reference system. Output overwrites the existing file.
	 * 
	 * @param pointIn
	 *            Le CSV contenant les aménités à trier
	 * @param pointVille
	 *            La liste des villes
	 * @return
	 * @throws IOException
	 * @throws FactoryException 
	 * @throws NoSuchAuthorityCodeException 
	 * @throws TransformException 
	 * @throws NumberFormatException 
	 * @throws MismatchedDimensionException 
	 */
	public static File preparePoint(File pointIn, File pointVille, File pointOut, String src, String communityCodeFiledName) throws IOException, NoSuchAuthorityCodeException, FactoryException, MismatchedDimensionException, NumberFormatException, TransformException {
		
		CSVReader csvVille = new CSVReader(new FileReader(pointVille));
		CSVReader csvAm = new CSVReader(new FileReader(pointIn));
		List<String[]> listVille = csvVille.readAll();
		List<String[]> listAm = csvAm.readAll();

		// get zipcodes indices
		int numCodPostAmenity = Attribute.getIndice(listAm.get(0), communityCodeFiledName);
		int numCodPost = Attribute.getIndice(listVille.get(0), DataImporter.getNameFieldCodeCommunity());

		// collection pour éliminer les doublons
		ArrayList<String> deleteDouble = new ArrayList<>();

		// copie des points sélectionnées dans un nouveau csv
		CSVWriter csv2copy = new CSVWriter(new FileWriter(pointOut, false));
		csv2copy.writeNext((String[]) listAm.get(0));
		for (String[] rowVille : listVille) {
			String codePost = rowVille[numCodPost];
			if (!deleteDouble.contains(codePost)) {
				for (String[] rowAmenity : listAm) {
					if (codePost.toUpperCase().equals(rowAmenity[numCodPostAmenity].toUpperCase())) {
						csv2copy.writeNext(rowAmenity);
					}
				}
			}
			deleteDouble.add(codePost);
		}
		csv2copy.close();
		csvVille.close();
		csvAm.close();
		
		if (src != DataImporter.getMainSRC()) {
			CSVReader csvChangeCoordTmp = new CSVReader(new FileReader(Files.copy(pointOut.toPath(), new File(tmpFolder,"preparePointReproj.csv").toPath(), StandardCopyOption.REPLACE_EXISTING).toFile()));
			CSVWriter csvChangeCoord = new CSVWriter(new FileWriter(pointOut, true));
			List<String[]> listEntries = csvChangeCoordTmp.readAll();
			MathTransform transform = CRS.findMathTransform(CRS.decode("EPSG:" + src), CRS.decode("EPSG:" + DataImporter.getMainSRC()), true);
			String[] firstLine = listEntries.remove(0);
			csvChangeCoord.writeNext(firstLine);
			int nColX = Attribute.getLatIndice(firstLine);
			int nColY = Attribute.getLongIndice(firstLine);
			for (String[] row : listEntries) {
				// copy the same attributes
				String[] result = row;
				DirectPosition2D ptDst = new DirectPosition2D();
				String x = row[nColX];
				String y = row[nColY];
				if (x != null && !x.isEmpty() && x != "" && y != null && !y.isEmpty() && y != "") {
					transform.transform(new DirectPosition2D(Double.valueOf(x), Double.valueOf(y)), ptDst);
					result[nColX] = String.valueOf(ptDst.getX());
					result[nColY] = String.valueOf(ptDst.getY());
					csvChangeCoord.writeNext(result);
				}
			}
			csvChangeCoordTmp.close();
			csvChangeCoord.close();
		}
		return pointOut;
	}

	public static void sortBPE(String[] bpeHeader, File bpeFile)
			throws IOException, MismatchedDimensionException, NoSuchAuthorityCodeException, FactoryException,
			ParseException, TransformException {
		sortBPE(bpeHeader, bpeFile, "separateField", Attribute.getLatIndice(bpeHeader),
				Attribute.getLongIndice(bpeHeader), Attribute.getIndice(bpeHeader, DataImporter.getBpeType()));
	}

}
