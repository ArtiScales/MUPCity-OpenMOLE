package fr.ign.task;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.data.store.ContentFeatureCollection;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.operation.transform.AffineTransform2D;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.thema.common.swing.TaskMonitor;
import org.thema.mupcity.Project;
import org.thema.mupcity.rule.OriginDistance;

import com.vividsolutions.jts.geom.Geometry;

public class ProjectCreationAndDecompositionTask {

	public static String NAME_BUILD_FILE = "BATI_AU.shp";
	public static String NAME_FILE_ROAD = "route_sans_chemin.shp";
	public static String NAME_FILE_FACILITY = "CS_au_besac_sirene_2012.shp";
	public static String NAME_FILE_LEISURE = "loisirs.shp";
	public static String NAME_FILE_BUS_STATION = "stations_besac_tram_2015.shp";
	public static String NAME_FILE_TRAIN = "gare_train_ICONE_docs_2015.shp";
	public static String NAME_FILE_NON_BUILDABLE = "non_urba.shp";

	public static void main(String[] args) throws Exception {
		String name = "Project";
		File folderIn = new File("./data/");
		File folderOut = new File("./result/");
		double width = 28303;
		double height = 21019;
		double xmin = 914760;
		double ymin = 6680157;
		double shiftX = 50;
		double shiftY = 50;

		double minSize = 20;
		double maxSize = 5000;
		double seuilDensBuild = 0;
		ProjectCreationAndDecompositionTask.run(name, folderIn, folderOut, xmin, ymin, width, height, shiftX, shiftY, minSize, maxSize, seuilDensBuild);
	}

	public static File run(String name, File folderIn, File folderOut, double xmin, double ymin, double width, double height, double shiftX, double shiftY, double minSize, double maxSize, double seuilDensBuild) throws Exception {
		TaskMonitor mon = new TaskMonitor.EmptyMonitor();
		folderOut.mkdirs();
		File buildFile = new File(folderOut, NAME_BUILD_FILE);
		File roadFile = new File(folderOut, NAME_FILE_ROAD);
		File facilityFile = new File(folderOut, NAME_FILE_FACILITY);
		File leisureFile = new File(folderOut, NAME_FILE_LEISURE);
		File busFile = new File(folderOut, NAME_FILE_BUS_STATION);
		File trainFile = new File(folderOut, NAME_FILE_TRAIN);
		File restrictFile = new File(folderOut, NAME_FILE_NON_BUILDABLE);
		// Translation des différentes couches
		translateSHP(new File(folderIn, NAME_BUILD_FILE), buildFile, shiftX, shiftY);
		translateSHP(new File(folderIn, NAME_FILE_ROAD), roadFile, shiftX, shiftY);
		translateSHP(new File(folderIn, NAME_FILE_FACILITY), facilityFile, shiftX, shiftY);
		translateSHP(new File(folderIn, NAME_FILE_LEISURE), leisureFile, shiftX, shiftY);
		translateSHP(new File(folderIn, NAME_FILE_BUS_STATION), busFile, shiftX, shiftY);
		translateSHP(new File(folderIn, NAME_FILE_TRAIN), trainFile, shiftX, shiftY);
		translateSHP(new File(folderIn, NAME_FILE_NON_BUILDABLE), restrictFile, shiftX, shiftY);
		// Creation du projet dans le dossier de données translaté
		Project project = Project.createProject(name, folderOut, buildFile, xmin, ymin, width, height, mon);
		project.setNetPrecision(0.1);
		// Définition des layers du projet
		boolean network = true;//always true?
		List<String> roadAttrs = Arrays.asList("Speed");// SPEED(numeric)
		project.setLayer(Project.LAYERS.get(Project.Layers.ROAD.ordinal()), roadFile, roadAttrs);
		List<String> facilityAttrs = Arrays.asList("LEVEL", "TYPE");// LEVEL(numeric),TYPE(any)
		project.setLayer(Project.LAYERS.get(Project.Layers.FACILITY.ordinal()), facilityFile, facilityAttrs);
		List<String> leisureAttrs = Arrays.asList("LEVEL", "TYPE");// LEVEL(numeric),TYPE(any)
		project.setLayer(Project.LAYERS.get(Project.Layers.LEISURE.ordinal()), leisureFile, leisureAttrs);
		List<String> emptyAttrs = Arrays.asList("");
		project.setLayer(Project.LAYERS.get(Project.Layers.BUS_STATION.ordinal()), busFile, emptyAttrs);
		project.setLayer(Project.LAYERS.get(Project.Layers.TRAIN_STATION.ordinal()), trainFile, emptyAttrs);
		project.setLayer(Project.LAYERS.get(Project.Layers.RESTRICT.ordinal()), restrictFile, emptyAttrs);
		project.setDistType((network) ? OriginDistance.NetworkDistance.class : OriginDistance.EuclideanDistance.class);
		project.decomp(3, maxSize, minSize, seuilDensBuild, mon, false);
		project.save();
		return new File(folderOut, name);
	}

	private static void translateSHP(File fileIn, File fileOut, double shiftX, double shiftY) throws Exception {
		ShapefileDataStore dataStore = new ShapefileDataStore(fileIn.toURI().toURL());
		AffineTransform2D translate = new AffineTransform2D(1, 0, 0, 1, shiftX, shiftY);
		ContentFeatureCollection shpFeatures = dataStore.getFeatureSource().getFeatures();
		SimpleFeatureType ft = dataStore.getSchema();
		DefaultFeatureCollection newFeatures = new DefaultFeatureCollection();
		Object[] nouveaux = new Object[shpFeatures.size()];
		int cpt = 0;
		SimpleFeatureIterator iterator = shpFeatures.features();
		try {
			while (iterator.hasNext()) {
				SimpleFeature feature = iterator.next();
				Geometry geom = (Geometry) feature.getDefaultGeometry();
				Geometry geomTransformed = JTS.transform(geom, translate);
				feature.setDefaultGeometry(geomTransformed);
				nouveaux[cpt] = feature;
				newFeatures.add(feature);
				cpt = +1;
			}
		} finally {
			iterator.close();
		}
		dataStore.dispose();
		ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
		Map<String, Serializable> params = new HashMap<>();
		params.put("url", fileOut.toURI().toURL());
		params.put("create spatial index", Boolean.TRUE);
		ShapefileDataStore newDataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);
		/*
		 * TYPE is used as a template to describe the file contents
		 */
		newDataStore.createSchema(ft);
		Transaction transaction = new DefaultTransaction("create");
		String typeName = newDataStore.getTypeNames()[0];
		SimpleFeatureSource featureSource = newDataStore.getFeatureSource(typeName);
		if (featureSource instanceof SimpleFeatureStore) {
			SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;
			featureStore.setTransaction(transaction);
			try {
				featureStore.addFeatures(newFeatures);
				transaction.commit();
			} catch (Exception problem) {
				problem.printStackTrace();
				transaction.rollback();
			} finally {
				transaction.close();
				newDataStore.dispose();
			}
		} else {
			System.out.println(typeName + " does not support read/write access");
			System.exit(1);
		}
	}
}