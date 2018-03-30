package fr.ign.task;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.MutablePair;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureWriter;
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

import fr.ign.exp.DataSetSelec;

public class ProjectCreationDecompTask {

	public static ClassLoader getClassLoader() {
		return ProjectCreationDecompTask.class.getClassLoader();
	}

	public static String nameProj;

	public static void main(String[] args) throws Exception {

		String name = "ProjectLol";
		File folderIn = new File("data");
		File folderOut = new File("result");
		double width = 26590;
		double height = 26590;
		double xmin = 915948;
		double ymin = 6677337;
		double shiftX = 50;
		double shiftY = 50;
		double minSize = 20;
		double maxSize = 43740;
		double seuilDensBuild = 0;

		DataSetSelec.predefSet();
		Map<String, String> dataHTproj = DataSetSelec.get("Data1.2");
		run(name, folderIn, folderOut, xmin, ymin, width, height, shiftX, shiftY, dataHTproj, maxSize, minSize, seuilDensBuild);
	}

	public static File run(String name, File folderIn, File folderOut, double xmin, double ymin, double width, double height, double shiftX, double shiftY, double maxSize,
			double minSize, double seuilDensBuild) throws Exception {

		Map<String, String> dataHT = DataSetSelec.dig(folderIn);
		return run(name, folderIn, folderOut, xmin, ymin, width, height, shiftX, shiftY, dataHT, maxSize, minSize, seuilDensBuild);
	}

	public static File run(String name, File folderIn, File folderOut, double xmin, double ymin, double width, double height, double shiftX, double shiftY,
			Map<String, String> dataHT, double maxSize, double minSize, double seuilDensBuild) throws Exception {
		File result = run(name, folderIn, folderOut, xmin, ymin, width, height, shiftX, shiftY, dataHT, maxSize, minSize, seuilDensBuild, true).getRight();
		System.out.println("project file : "+result);
				return result;
	}

	public static MutablePair<String, File> run(String name, File folderIn, File folderOut, double xmin, double ymin, double width, double height, double shiftX, double shiftY,
			double maxSize, double minSize, double seuilDensBuild, boolean machineReadable) throws Exception {

		Map<String, String> dataHT = DataSetSelec.dig(folderIn);
		return run(name, folderIn, folderOut, xmin, ymin, width, height, shiftX, shiftY, dataHT, maxSize, minSize, seuilDensBuild, machineReadable);
	}

	public static MutablePair<String, File> run(String name, File folderIn, File folderOut, double xmin, double ymin, double width, double height, double shiftX, double shiftY,
			Map<String, String> dataHT, double maxSize, double minSize, double seuilDensBuild, boolean machineReadable) throws Exception {
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

		// complete the name

		nameProj = name + "-" + dataHT.get("name") + "-CM" + minSize + "-S" + seuilDensBuild + "-GP_" + xmin + "_" + ymin;

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
		long start = System.currentTimeMillis();
		System.out.println("Translating layers in " + folderIn);

		translateSHP(new File(folderIn, dataHT.get("build")), buildFile, shiftX, shiftY);
		translateSHP(new File(folderIn, dataHT.get("road")), roadFile, shiftX, shiftY);
		translateSHP(new File(folderIn, dataHT.get("fac")), facilityFile, shiftX, shiftY);
		translateSHP(new File(folderIn, dataHT.get("lei")), leisureFile, shiftX, shiftY);
		translateSHP(new File(folderIn, dataHT.get("ptTram")), busFile, shiftX, shiftY);
		translateSHP(new File(folderIn, dataHT.get("ptTrain")), trainFile, shiftX, shiftY);

		if (useNU) {
			translateSHP(new File(folderIn, dataHT.get("nU")), restrictFile, shiftX, shiftY);
		}

		long end = System.currentTimeMillis();
		System.out.println("Translation in " + (end - start) + " ms");
		System.out.println("Creating project");
		// Creation du projet dans le dossier de données translaté
		Project project = Project.createProject(nameProj, folderOut, buildFile, xmin, ymin, width, height);
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
		System.out.println("Saving project");
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

		System.out.println("Decomposition");
		project.decomp(3, maxSize, minSize, seuilDensBuild, mon, false);

		System.out.println("Saving project");
		project.save();
		System.out.println("Cleanup");
		cleanProject(project);
		System.out.println("Finished creation of project " + getName());

		MutablePair<String, File> result = new MutablePair<String, File>(nameProj, new File(folderOut,nameProj));

		return result;
	}

	public static String getName() {
		return nameProj;
	}

	public static void cleanProject(Project project) throws IOException {

		// TODO mettre propre : vraiment pas beau mais je jette l'éponge sur les milliers de types d'objets différents pour pouvoir retrouver le shapefile des layers
		for (File f : project.getDirectory().listFiles()) {
			if (f.getName().endsWith(".shp") || f.getName().endsWith(".dbf") || f.getName().endsWith(".fix") || f.getName().endsWith(".prj") || f.getName().endsWith(".shx")) {
				Files.delete(f.toPath());
			}
		}

	}

	private static void translateSHP(File fileIn, File fileOut, double shiftX, double shiftY) throws Exception {
		translateSHP2(fileIn, fileOut, shiftX, shiftY);
	}

	private static void translateSHP1(File fileIn, File fileOut, double shiftX, double shiftY) throws Exception {
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
				cpt += 1;
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

	private static void translateSHP2(File fileIn, File fileOut, double shiftX, double shiftY) throws Exception {
		ShapefileDataStore dataStore = new ShapefileDataStore(fileIn.toURI().toURL());
		AffineTransform2D translate = new AffineTransform2D(1, 0, 0, 1, shiftX, shiftY);
		ContentFeatureCollection shpFeatures = dataStore.getFeatureSource().getFeatures();
		// DefaultFeatureCollection newFeatures = new DefaultFeatureCollection();
		// Object[] nouveaux = new Object[shpFeatures.size()];
		// int cpt = 0;
		ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
		Map<String, Serializable> params = new HashMap<>();
		params.put("url", fileOut.toURI().toURL());
		params.put("create spatial index", Boolean.TRUE);
		ShapefileDataStore newDataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);
		newDataStore.createSchema(dataStore.getSchema());
		Transaction transaction = new DefaultTransaction("create");
		FeatureWriter<SimpleFeatureType, SimpleFeature> writer = newDataStore.getFeatureWriterAppend(dataStore.getSchema().getTypeName(), transaction);
		SimpleFeatureIterator iterator = shpFeatures.features();
		try {
			while (iterator.hasNext()) {
				SimpleFeature feature = iterator.next();
				SimpleFeature copy = writer.next();
				copy.setAttributes(feature.getAttributes());
				Geometry geometry = (Geometry) feature.getDefaultGeometry();
				Geometry geometry2 = JTS.transform(geometry, translate);
				copy.setDefaultGeometry(geometry2);
				writer.write();
			}
			transaction.commit();
		} catch (Exception problem) {
			problem.printStackTrace();
			transaction.rollback();
			System.out.println("Export to shapefile failed");
		} finally {
			writer.close();
			iterator.close();
			transaction.close();
			dataStore.dispose();
			newDataStore.dispose();
		}
	}
}
