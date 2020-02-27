package fr.ign.tools.dataImporter;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import au.com.bytecode.opencsv.CSVReader;
import fr.ign.cogit.geoToolsFunctions.Attribute;
import fr.ign.cogit.geoToolsFunctions.Schemas;
import fr.ign.cogit.geoToolsFunctions.vectors.Collec;
import fr.ign.cogit.geoToolsFunctions.vectors.Geom;
import fr.ign.cogit.geoToolsFunctions.vectors.Shp;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Chargeur;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Groupe;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileWriter;

public class Tools {
	public static void deleteIsolatedRoadSections(File roadFile, File outFile) throws NoSuchAuthorityCodeException, FactoryException{
		// delete the segments which are not linked to the main road network -- uses of geox tool coz I failed with geotools graphs. Conectors between objects still broken

		IFeatureCollection<IFeature> featColl = ShapefileReader.read(roadFile.toString());

		CarteTopo cT = new CarteTopo("Network");
		double tolerance = 0.0;
		Chargeur.importAsEdges(featColl, cT, "", null, "", null, null, tolerance);

		Groupe gr = cT.getPopGroupes().nouvelElement();
		gr.setListeArcs(cT.getListeArcs());
		gr.setListeFaces(cT.getListeFaces());
		gr.setListeNoeuds(cT.getListeNoeuds());

		// on récupère les différents groupes
		List<Groupe> lG = gr.decomposeConnexes();
		Groupe zeGroupe = Collections.max(lG, Comparator.comparingInt(g -> g.getListeArcs().size()));
		IFeatureCollection<IFeature> featC = new FT_FeatureCollection<>();
		for (Arc a : zeGroupe.getListeArcs()) {
			featC.add(a.getCorrespondant(0));
		}
		ShapefileWriter.write(featC,  outFile.toString(), CRS.decode("EPSG:2154"));
	}
	
	/**
	 * merge every shapefile in a folderIn type folder where every information of
	 * the BD TOPO is stored into a separate folder
	 * 
	 * @param nom
	 * @param fileOut
	 * @return
	 * @throws Exception
	 */
	public static void mergeMultipleBdTopo(File folderIn, File empriseFile) throws Exception {
		HashMap<String, List<File>> lists = new HashMap<String, List<File>>();
		for (File folderDeptIn : folderIn.listFiles()) {
			if (folderDeptIn.isDirectory()) {
				for (File shapeFile : folderDeptIn.listFiles()) {
					if (shapeFile.toString().toLowerCase().endsWith(".shp")) {
						String name = shapeFile.getName().replace(".shp", "").replace(".SHP", "");
						List<File> tmpList = lists.containsKey(name) ? lists.get(name) : new ArrayList<File>();
						tmpList.add(shapeFile);
						lists.put(name, tmpList);
					}
				}
			}
		}
		for (String key : lists.keySet()) {
			List<File> listFile = lists.get(key);
			if (listFile.isEmpty()) {
				continue;
			}
			Shp.mergeVectFiles(listFile, new File(folderIn, key), empriseFile, true);
		}
	}
	/**
	 * créée une emprise en fonction de la liste des villes contenus dans le fichier adminFile. Le fichier retouré est utilisé pour le découpage des shapefiles provenant de la
	 * BDTopo.
	 * 
	 * @param rootFile:
	 *            dossier principal ou sont entreposées les données
	 * @param adminFile:
	 *            fichier csv listant les villes de notre cas d'étude
	 * @return un shapefile contenant l'emprise de l'étude
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ParseException
	 * @throws NoSuchAuthorityCodeException
	 * @throws FactoryException
	 */
	public static File createEmpriseFile(File inFolder, File adminFile) throws MalformedURLException, IOException, ParseException, NoSuchAuthorityCodeException, FactoryException {

		ShapefileDataStore geoFlaSDS = new ShapefileDataStore((new File(inFolder, "admin/commune.shp")).toURI().toURL());
		SimpleFeatureCollection geoFlaSFC = geoFlaSDS.getFeatureSource().getFeatures();
		CSVReader listVilleReader = new CSVReader(new FileReader(adminFile));
		DefaultFeatureCollection villeColl = new DefaultFeatureCollection();
		DefaultFeatureCollection emprise = new DefaultFeatureCollection();

		final int numInsee = Attribute.getINSEEIndice(listVilleReader.readNext());

		for (String[] row : listVilleReader.readAll()) {
			Arrays.stream(geoFlaSFC.toArray(new SimpleFeature[0])).forEach(feat -> {
				if (row[numInsee].equals(feat.getAttribute("INSEE_COM"))) {
					villeColl.add(feat);
		}});}
		SimpleFeatureBuilder sfBuilder = Schemas.getBasicSchema("emprise");
		sfBuilder.add(Geom.unionSFC(villeColl).buffer(3000));
		emprise.add(sfBuilder.buildFeature(null));

		listVilleReader.close();
		geoFlaSDS.dispose();

		return Collec.exportSFC(emprise.collection(), new File(inFolder, "emprise.shp"));
	}
	
	/**
	 * Afin de calculer les points d'entrées aux foret, le protocole suivant est utilisé : Création d’un buffer de 10m autour de la végétation de type \textit{Bois, Forêt fermée de
	 * feuillus, Forêt fermée de conifères,Forêt ouverte,Forêt fermée mixte, Zone arborée} et de surface supérieure à un hectare. Sélection des couches chemins et routes en inclus
	 * dans la bounding box de chacune de ces entités végétations. Chaque intersection des couches linéaires chemin et routes donnera un point d'accès à la forêt. Si il sont
	 * compris dans l’emprise de la forêt, sélection du point selon avec un type et une fréquence d'utilisation dépendant de la surface de la foret (si 1Ha<surface<2Ha, fréquence
	 * quotidienne, si 2Ha<surface<100Ha, fréquence hebdomadaire, si surface>100Ha, fréquence mensuelle).
	 * 
	 * @param vegetFile
	 *            : shapefile extrait de la BDTopo contenant les couches de végétation
	 * @param routeFile
	 *            : shapefile extrait de la BDTopo contenant les tronçons routiers
	 * @param cheminFile
	 *            : shapefile extrait de la BDTopo contenant les chemins
	 * @return : shapefile contenant les points d'entrées aux forêts.
	 * @throws Exception
	 */
	public static File createLeisureAccess(File vegetFile, File routeFile, File cheminFile) throws Exception {
		// Minimal area for a vegetation feature to be considered as an loisir resort is a half of an hectare
		int minArea = 10000;	
		ShapefileDataStore vegetSDS = new ShapefileDataStore(vegetFile.toURI().toURL());
		//TODO set charset (verify if it's ok?)
		System.out.println("createLeisureAccess charset tests"+vegetSDS.getCharset());
		
		vegetSDS.setCharset(Charset.forName("UTF-8"));
		SimpleFeatureCollection veget = vegetSDS.getFeatureSource().getFeatures();
		ShapefileDataStore routeSDS = new ShapefileDataStore(routeFile.toURI().toURL());
		SimpleFeatureCollection route = routeSDS.getFeatureSource().getFeatures();
		ShapefileDataStore cheminSDS = new ShapefileDataStore(cheminFile.toURI().toURL());
		SimpleFeatureCollection chemin = cheminSDS.getFeatureSource().getFeatures();

		DefaultFeatureCollection vegetDFC = new DefaultFeatureCollection();
		SimpleFeatureBuilder sfBuilder = Schemas.getMUPAmenitySchema("leisure");

		int i = 0;
		SimpleFeatureIterator featIt = veget.features();
		try {
			while (featIt.hasNext()) {
				SimpleFeature feat = featIt.next();
				Object[] attr = { "", 0 };
				// TODO débuger l'encodage (pas arrivé -- pas le temps)
				if (feat.getAttribute("NATURE").equals("Bois") || feat.getAttribute("NATURE").equals("ForÃªt fermÃ©e de feuillus")
						|| feat.getAttribute("NATURE").equals("ForÃªt fermÃ©e de conifÃ¨res") || feat.getAttribute("NATURE").equals("ForÃªt fermÃ©e mixte")
						|| feat.getAttribute("NATURE").equals("ForÃªt ouverte") || feat.getAttribute("NATURE").equals("Zone arborÃ©e")) {
					if (((Geometry) feat.getDefaultGeometry()).getArea() > minArea) {
						if (((Geometry) feat.getDefaultGeometry()).getArea() < 20000) {
							attr[0] = "espace_vert_f1";
							attr[1] = 1;
						} else if (((Geometry) feat.getDefaultGeometry()).getArea() < 1000000) {
							attr[0] = "espace_vert_f2";
							attr[1] = 2;
						} else {
							attr[0] = "espace_vert_f3";
							attr[1] = 3;
						}
						sfBuilder.add((Geometry) feat.getDefaultGeometry());
						SimpleFeature feature = sfBuilder.buildFeature(String.valueOf(i), attr);
						vegetDFC.add(feature);
						i = i + 1;
					}
				}
			}
		} catch (Exception problem) {
			problem.printStackTrace();
		} finally {
			featIt.close();
		}

		DefaultFeatureCollection loisirColl = new DefaultFeatureCollection();
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
		SimpleFeatureBuilder pointSfBuilder = Schemas.getMUPAmenitySchema("leisure") ;
		int cpt = 0;
		// selection of the intersection points into those zones
		int j = 0;
		SimpleFeatureIterator vegetIt = vegetDFC.features();
		try {
			while (vegetIt.hasNext()) {
				cpt = cpt + 1;
				SimpleFeature featForet = vegetIt.next();
				// snap of the wanted data
				SimpleFeatureCollection snapRoute = Collec.snapDatas(route, ((Geometry) featForet.getDefaultGeometry()).buffer(15));
				SimpleFeatureCollection snapChemin = Collec.snapDatas(chemin, ((Geometry) featForet.getDefaultGeometry()).buffer(15));
				SimpleFeatureIterator routeIt = snapRoute.features();
				try {
					while (routeIt.hasNext()) {
						SimpleFeature featRoute = routeIt.next();
						SimpleFeatureIterator itChemin = snapChemin.features();
						try {
							while (itChemin.hasNext()) {
								SimpleFeature featChemin = itChemin.next();
								Coordinate[] coord = ((Geometry) featChemin.getDefaultGeometry()).intersection((Geometry) featRoute.getDefaultGeometry()).getCoordinates();
								for (Coordinate co : coord) {
									Point point = geometryFactory.createPoint(co);
									if ((((Geometry) featForet.getDefaultGeometry()).buffer(15)).contains(point)) {
										pointSfBuilder.add(point);
										Object[] att = { featForet.getAttribute("TYPE"), featForet.getAttribute("LEVEL") };
										SimpleFeature feature = pointSfBuilder.buildFeature(String.valueOf(j), att);
										loisirColl.add(feature);
										j = j + 1;
									}
								}
							}
						} catch (Exception problem) {
							problem.printStackTrace();
						} finally {
							itChemin.close();
						}
					}
				} catch (Exception problem) {
					problem.printStackTrace();
				} finally {
					routeIt.close();
				}
			}
		} catch (Exception problem) {
			problem.printStackTrace();
		} finally {
			vegetIt.close();
		}
		routeSDS.dispose();
		cheminSDS.dispose();
		vegetSDS.dispose();

		return Collec.exportSFC(loisirColl.collection(), new File(vegetFile.getParentFile().getParentFile().getParentFile(), "tmp/loisir2.shp"));
	}
	
	public static Double[] getCoordFromCSV(String coordType, String row, String row2) {
		Double[] result = new Double[2];
		try {
			switch (coordType) {
			case "sameField":
				result[0] = Double.parseDouble(row.split(";")[0]);
				result[1] = Double.parseDouble(row.split(";")[1]);
				break;
			case "separateField":
				result[0] = Double.parseDouble(row);
				result[1] = Double.parseDouble(row2);
				break;
			}
		} catch (NumberFormatException n) {
			System.out.println("no coord for entity " + row + ". Return null");
			return null;
		}
		return result;
	}
	
	public static File setSpeed(File fileIn, File fileOut) throws Exception {
		ShapefileDataStore routesSDS = new ShapefileDataStore(fileIn.toURI().toURL());
		routesSDS.setCharset(Charset.forName("UTF-8"));
		SimpleFeatureIterator routeIt = routesSDS.getFeatureSource().getFeatures().features();
		SimpleFeatureBuilder sfBuilder = Schemas.getMUPRoadSchema();
		DefaultFeatureCollection roadDFC = new DefaultFeatureCollection();
		try {
			while (routeIt.hasNext()) {
				SimpleFeature feat = routeIt.next();
				String nature = (String) feat.getAttribute("NATURE");
				switch (nature) {
				case "Autoroute":
					sfBuilder.set("SPEED", 130); 
					break;
				case "Quasi-autoroute":
					sfBuilder.set("SPEED", 110);
					break;
				case "Bretelle":
					sfBuilder.set("SPEED", 50);
					break;
				case "Route à 1 chaussée":
				case "Route à 2 chaussées":
				case "Rond-point":
					String classement = (String) feat.getAttribute("CL_ADMIN");
					if (classement == null || classement.isEmpty()) {
						classement = "";
					}
					switch (classement) {
					case "Autre":
					case "":
						sfBuilder.set("SPEED", 40);
						break;
					default:
						sfBuilder.set("SPEED",  80);
					}
				default:
					continue;
				}
				sfBuilder.add((Geometry) feat.getDefaultGeometry());
				sfBuilder.set("NATURE", nature);
				roadDFC.add(sfBuilder.buildFeature(null));
			}
		} catch (Exception problem) {
			problem.printStackTrace();
		} finally {
			routeIt.close();
		}
		routesSDS.dispose();
		return Collec.exportSFC(roadDFC.collection(), fileOut);
	}
	
	public static File createPointFromCsv(File fileIn, File FileOut, File empriseFile, String name)
			throws IOException, NoSuchAuthorityCodeException, FactoryException, ParseException, MismatchedDimensionException, TransformException {

		boolean wkt = false;
		boolean mercador = false;

		WKTReader wktReader = new WKTReader();
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
		DefaultFeatureCollection coll = new DefaultFeatureCollection();
		SimpleFeatureBuilder pointSfBuilder = Schemas.getMUPAmenitySchema(name);

		CSVReader ptCsv = new CSVReader(new FileReader(fileIn));

		// case geocoded return lat/long
		CoordinateReferenceSystem epsg4326 = CRS.decode("EPSG:4326");
		CoordinateReferenceSystem epsg2154 = CRS.decode("EPSG:2154");
		MathTransform transform = CRS.findMathTransform(epsg4326, epsg2154, true);

		String[] firstLine = ptCsv.readNext();
		int mercLat = 0;
		int mercLng = 0;
		for (int i = 0; i < firstLine.length; i = i + 1) {
			String field = firstLine[i];
			if (field.equals("Lat")) {
				mercLat = i;
				mercador = true;
			}
			if (field.equals("Lng")) {
				mercLng = i;
			}
		}
		// case the geometry is a WKT String
		int nColWKT = 0;
		for (int i = 0; i < firstLine.length; i = i + 1) {
			String field = firstLine[i];
			if (field.contains("WKT")) {
				nColWKT = i;
				wkt = true;
				break;
			}
		}
		// case it's X and Y coordinates
		int nColX = 0;
		int nColY = 0;
		for (int i = 0; i < firstLine.length; i = i + 1) {
			String field = firstLine[i];
			if (field.equals("X")) {
				nColX = i;
			}
			if (field.equals("Y")) {
				nColY = i;
			}
		}
		// case it's geocoded from the BPE database
		for (int i = 0; i < firstLine.length; i = i + 1) {
			String field = firstLine[i];
			if (field.contains("lambert_x")) {
				nColX = i;
			}
			if (field.contains("lambert_y")) {
				nColY = i;
			}
		}
		
		int	nColType = Attribute.getIndice(firstLine, "TYPE");
		int nColLevel = Attribute.getIndice(firstLine, "LEVEL");
		// System.out.println("level colonne : " + nColLevel);
		// System.out.println("type colonne : " + nColType);
		// System.out.println("y colonne : " + nColY);
		// System.out.println("x colonne : " + nColX);
		Object[] attr = { 0, "" };
		int i = 0;
		for (String[] row : ptCsv.readAll()) {
			if (wkt) {
				System.out.println(row[nColX]+ row[nColY]);
				pointSfBuilder.add(wktReader.read(row[nColWKT]));
			}
			if (mercador) {
				System.out.println(row[nColX]+ row[nColY]);
				DirectPosition2D ptSrc = new DirectPosition2D(Double.valueOf(row[mercLat]), Double.valueOf(row[mercLng]));
				DirectPosition2D ptDst = new DirectPosition2D();
				transform.transform(ptSrc, ptDst);
				Point point = geometryFactory.createPoint(new Coordinate(ptDst.x, ptDst.y));
				pointSfBuilder.add(point);
			} else {
				System.out.println(row[nColX]+ row[nColY]);
				Point point = geometryFactory
						.createPoint(new Coordinate(Double.valueOf(row[nColX]), Double.valueOf(row[nColY])));
				pointSfBuilder.add(point);
			}
			attr[0] = row[nColType];
			attr[1] = row[nColLevel];
			SimpleFeature feature = pointSfBuilder.buildFeature(String.valueOf(i), attr);
			coll.add(feature);
			i = i + 1;
		}
		ptCsv.close();
		Collec.exportSFC(coll.collection(), new File("/home/mcolomb/tmp/tmp.shp"));
		return Collec.exportSFC(Collec.cropSFC(coll.collection(), empriseFile), FileOut);
	}
}