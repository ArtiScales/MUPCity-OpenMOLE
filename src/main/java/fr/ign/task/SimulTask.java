package fr.ign.task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;

//import javax.media.jai.JAI;
//import javax.media.jai.OperationRegistry;

import org.thema.mupcity.AHP;
import org.thema.mupcity.Project;
import org.thema.mupcity.scenario.ScenarioAuto;

//import com.sun.media.jai.imageioimpl.ImageReadWriteSpi;

public class SimulTask {

	public static ClassLoader getClassLoader() {
		return SimulTask.class.getClassLoader();
	}

	// static {
	// registry.registerServiceProvider(new URLImageInputStreamSpi());
	// }
	// protected static void initJAI() {
	//
	// // See [URL]http://docs.oracle.com/cd/E17802_01/products/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/OperationRegistry.html[/URL]
	// OperationRegistry registry = JAI.getDefaultInstance().getOperationRegistry();
	// if (registry == null) {
	// System.out.println("Error with JAI initialization (needed for GeoTools).");
	// } else {
	// try {
	// new ImageReadWriteSpi().updateRegistry(registry);
	// } catch (IllegalArgumentException e) {
	// // Probably indicates it was already registered.
	// }
	// }
	// }

	// save all the infos of the scenario+project
	public static boolean saveWholeProj = false;
	// save the synthetic evaluation layer
	public static boolean saveEval = true;
	public static boolean saveEvalAnal = true;
	public static String nameTot;
	public static String nameProj;

	public static void main(String[] args) throws Exception {
		File projFile = new File("/media/mcolomb/Data_2/resultFinal/testAHP/2emevague/StabiliteTestAHP-Autom-CM20.0-S0.0-GP_915948.0_6677337.0");

		String name = "StabiliteTestAHP-Autom-CM20.0-S0.0-GP_915948.0_6677337.0";
		saveEvalAnal=true;
		

		double ahp0 = 0.111;
		double ahp1 = 0.111;
		double ahp2 = 0.111;
		double ahp3 = 0.111;
		double ahp4 = 0.111;
		double ahp5 = 0.111;
		double ahp6 = 0.111;
		double ahp7 = 0.111;
		double ahp8 = 0.111;
		
//		 double ahp8 = 0.083;
//		 double ahp7 = 0.083;
//		 double ahp6 = 0.083;
//		 double ahp5 = 0.04;
//		 double ahp4 = 0.218;
//		 double ahp3 = 0.218;
//		 double ahp2 = 0.218;
//		 double ahp1 = 0.03;
//		 double ahp0 = 0.027;


		 
		// double ahp0 = 1;
		// double ahp1 = 1;
		// double ahp2 = 1;
		// double ahp3 = 1;
		// double ahp4 = 1;
		// double ahp5 = 1;
		// double ahp6 = 1;
		// double ahp7 = 1;
		// double ahp8 = 1;

		boolean strict = false;
		boolean mean = false;
		int nMax = 7;
		long seed = 26;
		
//		for (int i =0; i <= 50; i = i + 1) {
			seed = (long) (Math.random()*100000);
			run(projFile,new File("/media/mcolomb/Data_2/resultFinal/compThema"), name, nMax, strict, prepareAHP(ahp0, ahp1, ahp2, ahp3, ahp4, ahp5, ahp6, ahp7, ahp8),"ahpE", mean, seed, true);
//		}
	}

	public static File run(File decompFile, String name, int nMax, boolean strict, double ahp0, double ahp1, double ahp2, double ahp3, double ahp4, double ahp5, double ahp6,
			double ahp7, double ahp8, boolean mean, long seed, boolean machineReadable) throws Exception {
		File scenarOut = new File(decompFile, name);
		scenarOut.mkdir();
		return run(decompFile,scenarOut, name, nMax, strict, prepareAHP(ahp0, ahp1, ahp2, ahp3, ahp4, ahp5, ahp6, ahp7, ahp8), "ahpx", mean, seed, machineReadable);
	}

	public static File run(File decompFile, String name, int nMax, boolean strict, double ahp0, double ahp1, double ahp2, double ahp3, double ahp4, double ahp5, double ahp6,
			double ahp7, double ahp8, String ahpName, boolean mean, long seed, boolean machineReadable) throws Exception {
		File scenarOut = new File(decompFile, name);
		scenarOut.mkdir();
		return run(decompFile,scenarOut, name, nMax, strict, prepareAHP(ahp0, ahp1, ahp2, ahp3, ahp4, ahp5, ahp6, ahp7, ahp8), ahpName, mean, seed, machineReadable);
	}

	public static File run(File decompFile, File outFile, String name, int nMax, boolean strict, double ahp0, double ahp1, double ahp2, double ahp3, double ahp4, double ahp5, double ahp6,
			double ahp7, double ahp8, String ahpName, boolean mean, long seed, boolean machineReadable) throws Exception {
		return run(decompFile, outFile, name, nMax, strict, prepareAHP(ahp0, ahp1, ahp2, ahp3, ahp4, ahp5, ahp6, ahp7, ahp8), ahpName, mean, seed, machineReadable);
	}

	
	public static File run(File projectFile,File scenarOut, String name, int nMax, boolean strict, AHP ahp, String ahpName, boolean mean, long seed, boolean machineReadable) throws Exception {
		System.out.println("Initialization of " + projectFile);
		Initialize.init();
		System.out.println("Simulation of " + name);
		setName(name);
		Project project = Project.load(new File(projectFile, name + ".xml"));

		String nBa = "Ba";
		if (strict) {
			nBa = "St";
		}
		String nYag = "Yag";
		if (mean) {
			nYag = "Moy";
		}
		String nameScenar = "N" + String.valueOf(nMax) + "_" + nBa + "_" + nYag + ahpName + "_seed_" + String.valueOf(seed);

		if (machineReadable) {
			nameScenar = name + "--" + nameScenar;
		}

		System.out.println("simulation name = " + nameScenar);

		boolean useNU = true;

		System.out.println(project.getInfoLayer());
		if (!project.hasNoBuild()) {
			useNU = false;
		}

		NavigableSet<Double> res = project.getMSGrid().getResolutions();
		ScenarioAuto scenario = ScenarioAuto.createMultiScaleScenario(nameScenar, res.first(), res.last(), nMax, strict, ahp, useNU, mean, 3, seed, false, false);
		project.performScenarioAuto(scenario);
		
		if (saveEvalAnal) {
		scenario.extractEvalAnal(scenarOut, project);
		}
		if (saveEval) {
			project.getMSGrid().saveRaster(nameScenar + "-eval", scenarOut);
		}
		setName(ProjectCreationDecompTask.getName() + "_" + nameScenar);

		// save the project
		if (saveWholeProj) {
			scenario.save(scenarOut, project);
			scenario.extractEvalAnal(scenarOut, project);
			project.getMSGrid().saveRaster(nameScenar + "-eval", scenarOut);
		}
		System.out.println("scenar simu out : " + scenarOut);
		return scenarOut;
	}

	private static void setName(String newName) {
		nameTot = newName;
	}

	public static String getTotalName() {
		return nameTot;
	}

	public static String getProjectName() {
		return nameProj;
	}

	private static AHP prepareAHP(double ahp0, double ahp1, double ahp2, double ahp3, double ahp4, double ahp5, double ahp6, double ahp7, double ahp8) {
		List<String> items = new ArrayList<>();
		items.add("morpho");
		items.add("road");
		items.add("fac1");
		items.add("fac2");
		items.add("fac3");
		items.add("pt");
		items.add("lei1");
		items.add("lei2");
		items.add("lei3");
		AHP ahpE_Moy = new AHP(items);
		ahpE_Moy.setCoef(items.get(8), ahp8);
		ahpE_Moy.setCoef(items.get(7), ahp7);
		ahpE_Moy.setCoef(items.get(6), ahp6);
		ahpE_Moy.setCoef(items.get(5), ahp5);
		ahpE_Moy.setCoef(items.get(4), ahp4);
		ahpE_Moy.setCoef(items.get(3), ahp3);
		ahpE_Moy.setCoef(items.get(2), ahp2);
		ahpE_Moy.setCoef(items.get(1), ahp1);
		ahpE_Moy.setCoef(items.get(0), ahp0);
		return ahpE_Moy;
	}

}
