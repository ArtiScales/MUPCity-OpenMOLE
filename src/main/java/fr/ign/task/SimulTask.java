package fr.ign.task;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NavigableSet;

//import javax.media.jai.OperationRegistry;

import org.thema.mupcity.AHP;
import org.thema.mupcity.Project;
import org.thema.mupcity.scenario.ScenarioAuto;

import fr.ign.tools.OutputTools;
import fr.ign.tools.ScenarTools;

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

		String name = "StabiliteTestAHP-Autom-CM20.0-S0.0-GP_915948.0_6677337.0";
		saveEvalAnal = true;

		// setting on our six ahp objects
		HashMap<String, Double> toUse = new HashMap<String, Double>();
		HashMap<String, Double> ahpE_Moy = new HashMap<String, Double>();
		HashMap<String, Double> ahpS_Moy = new HashMap<String, Double>();
		HashMap<String, Double> ahpT_Moy = new HashMap<String, Double>();
		HashMap<String, Double> ahpE_Yag = new HashMap<String, Double>();// creation of the true objects
		HashMap<String, Double> ahpT_Yag = new HashMap<String, Double>();
		HashMap<String, Double> ahpS_Yag = new HashMap<String, Double>();

		ahpE_Yag.put("ahp8", 1.0);
		ahpE_Yag.put("ahp7", 1.0);
		ahpE_Yag.put("ahp6", 1.0);
		ahpE_Yag.put("ahp5", 1.0);
		ahpE_Yag.put("ahp4", 1.0);
		ahpE_Yag.put("ahp3", 1.0);
		ahpE_Yag.put("ahp2", 1.0);
		ahpE_Yag.put("ahp1", 1.0);
		ahpE_Yag.put("ahp0", 1.0);
		ahpE_Yag.put("ahpE_Yag", 99.0);

		ahpT_Yag.put("ahp8", 0.458);
		ahpT_Yag.put("ahp7", 0.458);
		ahpT_Yag.put("ahp6", 0.458);
		ahpT_Yag.put("ahp5", 3.625);
		ahpT_Yag.put("ahp4", 1.199);
		ahpT_Yag.put("ahp3", 1.199);
		ahpT_Yag.put("ahp2", 1.199);
		ahpT_Yag.put("ahp1", 0.202);
		ahpT_Yag.put("ahp0", 0.202);
		ahpT_Yag.put("ahpT_Yag", 99.0);

		ahpS_Yag.put("ahp8", 0.745);
		ahpS_Yag.put("ahp7", 0.745);
		ahpS_Yag.put("ahp6", 0.745);
		ahpS_Yag.put("ahp5", 0.359);
		ahpS_Yag.put("ahp4", 1.965);
		ahpS_Yag.put("ahp3", 1.965);
		ahpS_Yag.put("ahp2", 1.965);
		ahpS_Yag.put("ahp1", 0.269);
		ahpS_Yag.put("ahp0", 0.243);
		ahpS_Yag.put("ahpS_Yag", 99.0);

		ahpE_Moy.put("ahp8", 0.111);
		ahpE_Moy.put("ahp7", 0.111);
		ahpE_Moy.put("ahp6", 0.111);
		ahpE_Moy.put("ahp5", 0.111);
		ahpE_Moy.put("ahp4", 0.111);
		ahpE_Moy.put("ahp3", 0.111);
		ahpE_Moy.put("ahp2", 0.111);
		ahpE_Moy.put("ahp1", 0.111);
		ahpE_Moy.put("ahp0", 0.111);
		ahpE_Moy.put("ahpE_Moy", 99.0);

		ahpT_Moy.put("ahp8", 0.051);
		ahpT_Moy.put("ahp7", 0.051);
		ahpT_Moy.put("ahp6", 0.051);
		ahpT_Moy.put("ahp5", 0.403);
		ahpT_Moy.put("ahp4", 0.133);
		ahpT_Moy.put("ahp3", 0.133);
		ahpT_Moy.put("ahp2", 0.133);
		ahpT_Moy.put("ahp1", 0.022);
		ahpT_Moy.put("ahp0", 0.022);
		ahpT_Moy.put("ahpT_Moy", 99.0);

		ahpS_Moy.put("ahp8", 0.083);
		ahpS_Moy.put("ahp7", 0.083);
		ahpS_Moy.put("ahp6", 0.083);
		ahpS_Moy.put("ahp5", 0.04);
		ahpS_Moy.put("ahp4", 0.218);
		ahpS_Moy.put("ahp3", 0.218);
		ahpS_Moy.put("ahp2", 0.218);
		ahpS_Moy.put("ahp1", 0.03);
		ahpS_Moy.put("ahp0", 0.027);
		ahpS_Moy.put("ahpS_Moy", 99.0);

		// list of AHP to loop in
		List<HashMap<String, Double>> ahpList = new ArrayList<HashMap<String, Double>>();
		ahpList.add(ahpE_Yag);
		ahpList.add(ahpT_Yag);
		ahpList.add(ahpS_Yag);

		long seed = 42L;

		System.out.println("----------Simulation task----------");
		SimulTask.saveEval = true;

		//autom
		File projFile = new File("/media/mcolomb/Data_2/resultFinal/testAHP/2emevague/StabiliteTestAHP-Autom-CM20.0-S0.0-GP_915948.0_6677337.0");

		for (int j = 0; j <= 3; j++) {
			switch (j) {
			
			case 1:
				projFile = new File("");
				break;
			//
			case 2:
				projFile = new File("");
				break;
			case 3:
				projFile = new File("");
				break;

			}
			for (int i = 0; i <= 3; i++) {

				toUse = ahpE_Moy;

				int nMax = 4;
				boolean strict = true;
				boolean mean = true;
				switch (i) {
				case 1:
					nMax = 5;
					strict = false;
					break;
				case 2:
					nMax = 6;
					strict = true;
					break;
				case 3:
					nMax = 7;
					strict = false;
					mean = false;
					toUse = ahpE_Yag;
					break;

				}

				String ahpName = ScenarTools.getAHPName(toUse);
				seed = (long) (Math.random() * 100000);
				SimulTask.run(projFile, new File("/media/mcolomb/Data_2/resultFinal/testAHP/troisiemeVague"), name, nMax, strict, toUse.get("ahp0"), toUse.get("ahp1"),
						toUse.get("ahp2"), toUse.get("ahp3"), toUse.get("ahp4"), toUse.get("ahp5"), toUse.get("ahp6"), toUse.get("ahp7"), toUse.get("ahp8"), ahpName, mean, seed,
						false);
			}
		}

	}

	public static File run(File decompFile, String name, int nMax, boolean strict, double ahp0, double ahp1, double ahp2, double ahp3, double ahp4, double ahp5, double ahp6,
			double ahp7, double ahp8, boolean mean, long seed, boolean machineReadable) throws Exception {
		File scenarOut = new File(decompFile, name);
		scenarOut.mkdir();
		return run(decompFile, scenarOut, name, nMax, strict, prepareAHP(ahp0, ahp1, ahp2, ahp3, ahp4, ahp5, ahp6, ahp7, ahp8), "ahpx", mean, seed, machineReadable);
	}

	public static File run(File decompFile, String name, int nMax, boolean strict, double ahp0, double ahp1, double ahp2, double ahp3, double ahp4, double ahp5, double ahp6,
			double ahp7, double ahp8, String ahpName, boolean mean, long seed, boolean machineReadable) throws Exception {
		File scenarOut = new File(decompFile, OutputTools.niceScenarName(nMax, strict, mean, ahpName, seed));
		scenarOut.mkdir();
		return run(decompFile, scenarOut, name, nMax, strict, prepareAHP(ahp0, ahp1, ahp2, ahp3, ahp4, ahp5, ahp6, ahp7, ahp8), ahpName, mean, seed, machineReadable);
	}

	public static File run(File decompFile, File outFile, String name, int nMax, boolean strict, double ahp0, double ahp1, double ahp2, double ahp3, double ahp4, double ahp5,
			double ahp6, double ahp7, double ahp8, String ahpName, boolean mean, long seed, boolean machineReadable) throws Exception {
		return run(decompFile, outFile, name, nMax, strict, prepareAHP(ahp0, ahp1, ahp2, ahp3, ahp4, ahp5, ahp6, ahp7, ahp8), ahpName, mean, seed, machineReadable);
	}

	public static File run(File projectFile, File scenarOut, String name, int nMax, boolean strict, AHP ahp, String ahpName, boolean mean, long seed, boolean machineReadable)
			throws Exception {
		System.out.println("Initialization of " + projectFile);
		Initialize.init();
		System.out.println("Simulation of " + name);
		setName(name);
		Project project = Project.load(new File(projectFile, name + ".xml"));

		String nameScenar = OutputTools.niceScenarName(nMax, strict, mean, ahpName, seed);

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
