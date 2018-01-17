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
import org.thema.common.swing.TaskMonitor;
import org.thema.mupcity.Project;
import org.thema.mupcity.rule.OriginDistance;

import com.vividsolutions.jts.geom.Geometry;

import fr.ign.exp.DataSetSelec;

public class ProjectCreationTask {

	public static String nameProj;
	
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

		DataSetSelec.main(args);
		Map<String, String> dataHTproj = DataSetSelec.get("Data2.2");

		System.out.println(dataHTproj);

		ProjectCreationTask.run(name, folderIn, folderOut, xmin, ymin, width, height, shiftX, shiftY, dataHTproj);
	}
	
	

	public static File run(String name, File folderIn, File folderOut, double xmin, double ymin, double width, double height, double shiftX, double shiftY) throws Exception {
		Map<String, String> dataHT = DataSetSelec.dig(folderIn);
		return run( name,  folderIn,  folderOut,  xmin,  ymin,  width,  height,  shiftX,  shiftY,dataHT);
	}
	
	public static File run(String name, File folderIn, File folderOut, double xmin, double ymin, double width, double height, double shiftX, double shiftY,
			Map<String, String> dataHT) throws Exception {
		TaskMonitor mon = new TaskMonitor.EmptyMonitor();
		
		// Dossier intermédiaire avec les fichiers transformées
		// File folderTemp = new File(folderIn + "/tmp/");
		folderOut.mkdirs();
		File buildFile = new File(folderOut, dataHT.get("build"));
		File roadFile = new File(folderOut, dataHT.get("road"));
		File facilityFile = new File(folderOut, dataHT.get("fac"));
		File leisureFile = new File(folderOut, dataHT.get("lei"));
		File busFile = new File(folderOut, dataHT.get("ptTram"));
		File trainFile = new File(folderOut, dataHT.get("ptTrain"));
		File restrictFile = new File("");

		boolean useNU = true;
		if (dataHT.containsKey("nU")) {
			restrictFile = new File(folderOut, dataHT.get("nU"));

		} else {
			useNU = false;
			System.out.println("no non-urbanisable layer set");
		}

		//	complete the name
		
		nameProj = name +"-"+ dataHT.get("name");
		
		// put in line for the massacre
		File[] listMassacre = { buildFile, roadFile, facilityFile, leisureFile, busFile, trainFile, restrictFile };
		if (!useNU) {
			listMassacre = new File[6];
			listMassacre[0] = buildFile;
			listMassacre[1] = roadFile;
			listMassacre[2] = facilityFile;
			listMassacre[3] = leisureFile;
			listMassacre[4] = busFile;
			listMassacre[5] = trainFile;
		}

		// Translation des différentes couches

		translateSHP(new File(folderIn, dataHT.get("build")), buildFile, shiftX, shiftY);
		translateSHP(new File(folderIn, dataHT.get("road")), roadFile, shiftX, shiftY);
		translateSHP(new File(folderIn, dataHT.get("fac")), facilityFile, shiftX, shiftY);
		translateSHP(new File(folderIn, dataHT.get("lei")), leisureFile, shiftX, shiftY);
		translateSHP(new File(folderIn, dataHT.get("ptTram")), busFile, shiftX, shiftY);
		translateSHP(new File(folderIn, dataHT.get("ptTrain")), trainFile, shiftX, shiftY);
		if (useNU) {
			translateSHP(new File(folderIn, dataHT.get("nU")), restrictFile, shiftX, shiftY);
		}
		// Creation du projet dans le dossier de données translaté
		Project project = Project.createProject(nameProj, folderOut, buildFile, xmin, ymin, width, height, mon);
		project.setNetPrecision(0.1);
		// Définition des layers du projet
		boolean network = true;
		List<String> roadAttrs = Arrays.asList("Speed");// SPEED(numeric)
		project.setLayer(Project.LAYERS.get(Project.Layers.ROAD.ordinal()), roadFile, roadAttrs);
		List<String> facilityAttrs = Arrays.asList("LEVEL", "TYPE");// LEVEL(numeric),TYPE(any)
		project.setLayer(Project.LAYERS.get(Project.Layers.FACILITY.ordinal()), facilityFile, facilityAttrs);
		List<String> leisureAttrs = Arrays.asList("LEVEL", "TYPE");// LEVEL(numeric),TYPE(any)
		project.setLayer(Project.LAYERS.get(Project.Layers.LEISURE.ordinal()), leisureFile, leisureAttrs);
		List<String> emptyAttrs = Arrays.asList("");
		project.setLayer(Project.LAYERS.get(Project.Layers.BUS_STATION.ordinal()), busFile, emptyAttrs);
		project.setLayer(Project.LAYERS.get(Project.Layers.TRAIN_STATION.ordinal()), trainFile, emptyAttrs);
		if (useNU) {
			project.setLayer(Project.LAYERS.get(Project.Layers.RESTRICT.ordinal()), restrictFile, emptyAttrs);
		}
		project.setDistType((network) ? OriginDistance.NetworkDistance.class : OriginDistance.EuclideanDistance.class);
		project.save();

		// MASSACRE
		for (File f : listMassacre) {
			CharSequence target = f.getName().subSequence(0, f.getName().length() - 4);
			for (File fDelete : f.getParentFile().listFiles()) {
				if (fDelete.toString().contains(target)) {
					fDelete.delete();
				}
			}

		}
		return new File(folderOut, nameProj);
	}

	public static String getName(){
		return nameProj;
	}
	
	private static void translateSHP(File fileIn, File fileOut, double shiftX, double shiftY) throws Exception {
		ShapefileDataStore dataStore = new ShapefileDataStore(fileIn.toURI().toURL());
		AffineTransform2D translate = new AffineTransform2D(1, 0, 0, 1, shiftX, shiftY);
		ContentFeatureCollection shpFeatures = dataStore.getFeatureSource().getFeatures();
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
		ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
		Map<String, Serializable> params = new HashMap<>();
		params.put("url", fileOut.toURI().toURL());
		params.put("create spatial index", Boolean.TRUE);
		ShapefileDataStore newDataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);
		/*
		 * TYPE is used as a template to describe the file contents
		 */
		newDataStore.createSchema(dataStore.getSchema());
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
