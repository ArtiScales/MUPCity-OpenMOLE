package fr.ign.tools.dataImporter;

import java.io.File;
import java.io.FileNotFoundException;
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

import org.geotools.data.DataUtilities;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.operation.TransformException;

import au.com.bytecode.opencsv.CSVReader;
import fr.ign.artiscales.tools.geoToolsFunctions.Attribute;
import fr.ign.artiscales.tools.geoToolsFunctions.Schemas;
import fr.ign.artiscales.tools.geoToolsFunctions.vectors.Collec;
import fr.ign.artiscales.tools.geoToolsFunctions.vectors.Geom;
import fr.ign.artiscales.tools.geoToolsFunctions.vectors.Shp;
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
	/**
	 * Delete road segments that are isolated from the main 
	 * @param roadFile
	 * @param outFile
	 * @throws NoSuchAuthorityCodeException
	 * @throws FactoryException
	 */
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
	 * Merge every shapefile in a folderIn type folder where every information of the BD TOPO is stored into a separate folder
	 * @param nom
	 * @param fileOut
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	public static void mergeMultipleBdTopo(File folderIn, File empriseFile) throws IOException {
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
	 * crée une emprise en fonction de la liste des villes contenus dans le fichier adminFile. Le fichier retourné est utilisé pour le découpage des shapefiles provenant de la
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
	 * @throws FileNotFoundException 
	 */
	public static File createEmpriseFile(File inFolder, File adminFile) throws IOException, NoSuchAuthorityCodeException, FactoryException {
		ShapefileDataStore geoFlaSDS = new ShapefileDataStore((new File(inFolder, "admin/commune.shp")).toURI().toURL());
		CSVReader listVilleReader = new CSVReader(new FileReader(adminFile));
		DefaultFeatureCollection villeColl = new DefaultFeatureCollection();
		DefaultFeatureCollection emprise = new DefaultFeatureCollection();
		final int numInsee = Attribute.getIndice(listVilleReader.readNext(), DataImporter.getNameFieldCodeCommunity());
		for (String[] row : listVilleReader.readAll()) {
			Arrays.stream(geoFlaSDS.getFeatureSource().getFeatures().toArray(new SimpleFeature[0])).forEach(feat -> {
				if (row[numInsee].equals(feat.getAttribute(DataImporter.getNameFieldCodeGeoFla())))
					villeColl.add(feat);
		});}
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
	 *            Shapefile extrait de la BDTopo contenant les couches de végétation
	 * @param roadFile
	 *            Shapefile extrait de la BDTopo contenant les tronçons routiers
	 * @param cheminFile
	 *            Shapefile extrait de la BDTopo contenant les chemins
	 * @return Shapefile contenant les points d'entrées aux forêts.
	 * @throws FactoryException 
	 * @throws NoSuchAuthorityCodeException 
	 * @throws IOException 
	 */
	public static File createLeisureAccess(File vegetFile, File roadFile, File empriseFile, File tmpFolder) throws NoSuchAuthorityCodeException, FactoryException, IOException {
		ShapefileDataStore empriseSDS = new ShapefileDataStore(empriseFile.toURI().toURL());
		SimpleFeatureCollection emprise = DataUtilities.collection(empriseSDS.getFeatureSource().getFeatures());
		empriseSDS.dispose();
		ShapefileDataStore vegetSDS = new ShapefileDataStore(vegetFile.toURI().toURL());
		vegetSDS.setCharset(Charset.forName("UTF-8"));
		DefaultFeatureCollection vegetDFC = new DefaultFeatureCollection();
		
		SimpleFeatureTypeBuilder sfTypeBuilder = new SimpleFeatureTypeBuilder();
		sfTypeBuilder.setCRS(CRS.decode("EPSG:2154"));
		sfTypeBuilder.setName("vege");
		sfTypeBuilder.add("the_geom", Polygon.class);
		sfTypeBuilder.setDefaultGeometry("the_geom");
		sfTypeBuilder.add("TYPE", String.class);
		sfTypeBuilder.add("LEVEL", Integer.class);
		SimpleFeatureType pointFeatureType = sfTypeBuilder.buildFeatureType();
		SimpleFeatureBuilder sfBuilder = new SimpleFeatureBuilder(pointFeatureType);
		
		//classification of the green spaces with their sizes
		try (SimpleFeatureIterator featIt = Collec.snapDatas(vegetSDS.getFeatureSource().getFeatures(), emprise).features()) {
			while (featIt.hasNext()) {
				SimpleFeature feat = featIt.next();
				Geometry featGeom = (Geometry) feat.getDefaultGeometry();
				String nature = (String) feat.getAttribute("NATURE");
				if ((nature.equals("Bois") || nature.equals("Forêt fermée de feuillus") || nature.equals("Forêt fermée de conifères")
						|| nature.equals("Forêt fermée mixte") || nature.equals("Forêt ouverte") || nature.equals("Zone arborée"))
						&& featGeom.getArea() > 1000) {
					String type;
					int level;
					if (featGeom.getArea() < 20000) {
						type = "espace_vert_f1";
						level = 1;
					} else if (featGeom.getArea() < 1000000) {
						type = "espace_vert_f2";
						level = 2;
					} else {
						type = "espace_vert_f3";
						level = 3;
					}
					sfBuilder.add(featGeom);
					sfBuilder.set("TYPE", type);
					sfBuilder.set("LEVEL", level);
					vegetDFC.add(sfBuilder.buildFeature(Attribute.makeUniqueId()));
				}
			}
		} catch (Exception problem) {
			problem.printStackTrace();
		}
		vegetSDS.dispose();
Collec.exportSFC(vegetDFC, new File("/tmp/vege"));
		//make road infos
		FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
		ShapefileDataStore roadSDS = new ShapefileDataStore(roadFile.toURI().toURL());
		roadSDS.setCharset(Charset.forName("UTF-8"));
		SimpleFeatureCollection road = Collec.snapDatas(roadSDS.getFeatureSource().getFeatures(), emprise);
		List<Filter> filters = new ArrayList<Filter>(); 
		filters.add(ff.like(ff.property("NATURE"), "Chemin"));
		filters.add(ff.like(ff.property("NATURE"), "Sentier"));
		SimpleFeatureCollection chemin = DataUtilities.collection(road.subCollection(ff.or(filters)));
		filters.add(ff.like(ff.property("NATURE"), "Bretelle"));
		filters.add(ff.like(ff.property("NATURE"), "Escalier"));
		filters.add(ff.like(ff.property("NATURE"), "Type autoroutier" ));
		SimpleFeatureCollection route = DataUtilities.collection(road.subCollection(ff.not(ff.or(filters))));
		roadSDS.dispose();
		DefaultFeatureCollection leisureAccess = new DefaultFeatureCollection();
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
		SimpleFeatureBuilder pointSfBuilder = Schemas.getMUPAmenitySchema("leisure") ;
		// selection of the intersection points into those zones
		try (SimpleFeatureIterator vegetIt = vegetDFC.features()){
			while (vegetIt.hasNext()) {	
				SimpleFeature featForet = vegetIt.next();
				Geometry geomForet = ((Geometry) featForet.getDefaultGeometry()).buffer(15);
				// snap of the wanted data
				SimpleFeatureCollection snapRoute = Collec.snapDatas(route, geomForet);
				SimpleFeatureCollection snapChemin = Collec.snapDatas(chemin, geomForet);
				// loop on roads
				try (SimpleFeatureIterator routeIt = snapRoute.features()) {
					while (routeIt.hasNext()) {
						Geometry geomRoute = (Geometry) routeIt.next().getDefaultGeometry();
						// loop on trails
						try (SimpleFeatureIterator itChemin = Collec.snapDatas(snapChemin, geomRoute).features()) {
							trail: while (itChemin.hasNext()) {
								for (Coordinate co : ((Geometry) itChemin.next().getDefaultGeometry()).intersection(geomRoute).getCoordinates()) {
									Point point = geometryFactory.createPoint(co);
									if (geomForet.contains(point)) {
										pointSfBuilder.add(point);
										pointSfBuilder.set("TYPE", (String) featForet.getAttribute("TYPE"));
										pointSfBuilder.set("LEVEL", (int) featForet.getAttribute("LEVEL"));
										leisureAccess.add(pointSfBuilder.buildFeature(Attribute.makeUniqueId()));
										//we limit to one point per trail
										break trail;
									}
								}
							}
						} catch (Exception problem) {
							problem.printStackTrace();
						}
					}
				} catch (Exception problem) {
					problem.printStackTrace();
				} 
			}
		} catch (Exception problem) {
			problem.printStackTrace();
		} 
		return Collec.exportSFC(leisureAccess.collection(), new File(tmpFolder, "leisureAccess.shp"));
	}

	/**
	 * Sculpt the coordinates of points coming from string(s) to be easy to process
	 * 
	 * @param coordType
	 *            If the coordinate is into one or two String (usually tab cells). If the value is <b>sameField</b>, the coordinates are contained only in the {@param row} string
	 *            and values are separated with ';'
	 * @param row
	 * @param row2
	 * @return
	 */
	public static Double[] getCoordPointFromCSV(String coordType, String row, String row2) {
		Double[] result = new Double[2];
		try {
			switch (coordType) {
			case "sameField":
				result[0] = Double.parseDouble(row.split(";")[0]);
				result[1] = Double.parseDouble(row.split(";")[1]);
				break;
			default:
				result[0] = Double.parseDouble(row);
				result[1] = Double.parseDouble(row2);
				break;
			}
		} catch (NumberFormatException n) {
			return null;
		}
		return result;
	}
	
	public static File setSpeed(File fileIn, File fileOut) throws NoSuchAuthorityCodeException, FactoryException, IOException {
		ShapefileDataStore routesSDS = new ShapefileDataStore(fileIn.toURI().toURL());
		routesSDS.setCharset(Charset.forName("UTF-8"));
		SimpleFeatureBuilder sfBuilder = Schemas.getMUPRoadSchema();
		DefaultFeatureCollection roadDFC = new DefaultFeatureCollection();
		try (SimpleFeatureIterator routeIt = routesSDS.getFeatureSource().getFeatures().features()){
			while (routeIt.hasNext()) {
				SimpleFeature feat = routeIt.next();
				String nature = (String) feat.getAttribute("NATURE");
				switch (nature) {
				case "Autoroute":
				case "Type autoroutier":
					sfBuilder.set("SPEED", 130); 
					break;
				case "Quasi-autoroute":
					sfBuilder.set("SPEED", 110);
					break;
				case "Bretelle":
					sfBuilder.set("SPEED", 50);
					break;
//				case "Chemin":
//				case "Route empierrée":
//				case "Piste cyclable":
//					sfBuilder.set("SPEED", 10);
//					break;
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
						sfBuilder.set("SPEED", 80);
					}
					break;
				default:
					continue;
				}
				sfBuilder.add((Geometry) feat.getDefaultGeometry());
				sfBuilder.set("NATURE", nature);
				roadDFC.add(sfBuilder.buildFeature(null));
			}
		} catch (Exception problem) {
			problem.printStackTrace();
		} 
		routesSDS.dispose();
		return Collec.exportSFC(roadDFC.collection(), fileOut);
	}
	
	public static File createPointFromCsv(File fileIn, File fileOut, File empriseFile, String name) throws IOException, NoSuchAuthorityCodeException, FactoryException, MismatchedDimensionException, TransformException, ParseException	 {

		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
		DefaultFeatureCollection coll = new DefaultFeatureCollection();
		SimpleFeatureBuilder pointSfBuilder = Schemas.getMUPAmenitySchema(name);

		CSVReader ptCsv = new CSVReader(new FileReader(fileIn));
		String[] firstLine = ptCsv.readNext();

		// case it's X and Y coordinates
		int nColX = Attribute.getLatIndice(firstLine);
		int nColY = Attribute.getLongIndice(firstLine);
		
		// case the geometry is a WKT String
		boolean wkt = false;
		WKTReader wktReader = new WKTReader();
		int nColWKT = 0;
		for (int i = 0; i < firstLine.length; i = i + 1) {
			if (firstLine[i].contains("WKT")) {
				nColWKT = i;
				wkt = true;
				System.out.println(fileIn + " uses WKT geometries");
				break;
			}
		}
		int	n1 =0;
		int n2=0;
		switch (name) {
		case "leisure":
		case "service":
			n1 = Attribute.getIndice(firstLine, "TYPE");
			n2 = Attribute.getIndice(firstLine, "LEVEL");
			break;
		case "train":
			n1 = Attribute.getIndice(firstLine, "NATURE");
			break;
		}

		Object[] attr = { 0, "" };
		for (String[] row : ptCsv.readAll()) {
			if (wkt) {
				pointSfBuilder.add(wktReader.read(row[nColWKT]));
			} else {
				pointSfBuilder.add(geometryFactory.createPoint(new Coordinate(Double.valueOf(row[nColX]), Double.valueOf(row[nColY]))));
			}
			attr[0] = row[n1];
			attr[1] = row[n2];
			coll.add(pointSfBuilder.buildFeature(null, attr));
		}
		ptCsv.close();
		return Collec.exportSFC(Collec.snapDatas(coll.collection(), empriseFile), fileOut);
	}
}