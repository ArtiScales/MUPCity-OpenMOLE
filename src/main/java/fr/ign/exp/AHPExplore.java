package fr.ign.exp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.MutablePair;

import fr.ign.analyse.FractalDimention;
import fr.ign.task.AnalyseTask;
import fr.ign.task.ProjectCreationDecompTask;
import fr.ign.task.SimulTask;
import fr.ign.tools.DataSetSelec;
import fr.ign.tools.ScenarTools;

public class AHPExplore {

	public static void main(String[] args) throws Exception {

		DataSetSelec.predefSet();
		Map<String, String> dataHT = DataSetSelec.get("Data1");
		String name = "StabiliteTestAHP";
		File folderIn = new File("./stabilite/dataManu");
		File folderOut = new File("./result/testAHP");
		File discreteFile = new File(folderIn, "admin_typo.shp");
		File buildFile = new File(folderIn, "batimentPro.shp");
		double width = 26590;
		double height = 26590;
		// double width = 200;
		// double height = 200;
		double xmin = 915948;
		double ymin = 6677337;
		double shiftX = 0;
		double shiftY = 0;

		double minSize = 20;
		double maxSize = 14580;
		// double maxSize = 200;
		double seuilDensBuild = 0;

		// setting on our six ahp objects
		HashMap<String, Double> ahpE_Yag = new HashMap<String, Double>();// creation of the true objects
		HashMap<String, Double> ahpT_Yag = new HashMap<String, Double>();
		HashMap<String, Double> ahpS_Yag = new HashMap<String, Double>();
		HashMap<String, Double> ahpE_Moy = new HashMap<String, Double>();
		HashMap<String, Double> ahpS_Moy = new HashMap<String, Double>();
		HashMap<String, Double> ahpT_Moy = new HashMap<String, Double>();

		// HashMap<String, Double> coefsE_Yag = new HashMap<>();// collection of the eigenvector values
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
		ahpList.add(ahpE_Moy);
		ahpList.add(ahpT_Moy);
		ahpList.add(ahpS_Moy);

		long seed = 42L;

		boolean strict = false;

		MutablePair<String, File> projectFile = ProjectCreationDecompTask.run(name, folderIn, folderOut, xmin, ymin, width, height, shiftX, shiftY, dataHT, maxSize, minSize,
				seuilDensBuild, false);
		System.out.println("----------Simulation task----------");
		SimulTask.saveEval = false;

		for (int nMax = 3; nMax <= 7; nMax++) {
			for (int i = 0; i <= 1; i++) {
				if (i == 1) {
					strict = true;
				}

				for (HashMap<String, Double> list : ahpList) {
					String ahpName = ScenarTools.getAHPName(list);
					File fileScenar = SimulTask.run(projectFile.getRight(), projectFile.getLeft(), nMax, strict, list.get("ahp0"), list.get("ahp1"), list.get("ahp2"),
							list.get("ahp3"), list.get("ahp4"), list.get("ahp5"), list.get("ahp6"), list.get("ahp7"), list.get("ahp8"), ahpName, ScenarTools.setAgregMethod(list),
							seed, false);
					File fileMup = new File("");
					for (File f : fileMup.listFiles()) {
						if (f.getName().endsWith("evalAnal-20.0.tif")) {
							fileMup = f;
						}
					}
					FractalDimention.getCorrFracDim(new File("/media/mcolomb/Data_2/dataOpenMole/stabilite/dataManu/batimentPro.shp"), fileMup,
							new File(folderOut, "dimFractales.csv"), 10, String.valueOf(nMax) + "-" + ahpName);
				}

			}
		}

	}

	/**
	 * Method used if no dataSet is provided. The folderIn is dug to find matches with the wanted format
	 * 
	 * @throws Exception
	 */
	public static File run(String name, File folderIn, File folderOut, File discreteFile, File buildFile, double xmin, double ymin, double width, double height, double shiftX,
			double shiftY, double minSize, double maxSize, double seuilDensBuild, int nMax, boolean strict, double ahp0, double ahp1, double ahp2, double ahp3, double ahp4,
			double ahp5, double ahp6, double ahp7, double ahp8, String ahpName, boolean mean, long seed) throws Exception {

		return run(name, folderIn, folderOut, discreteFile, buildFile, xmin, ymin, width, height, shiftX, shiftY, minSize, maxSize, seuilDensBuild, nMax, strict, ahp0, ahp1, ahp2,
				ahp3, ahp4, ahp5, ahp6, ahp7, ahp8, ahpName, mean, seed, DataSetSelec.dig(folderIn));
	}

	public static File run(String name, File folderIn, File folderOut, File discreteFile, File buildFile, double xmin, double ymin, double width, double height, double shiftX,
			double shiftY, double minSize, double maxSize, double seuilDensBuild, int nMax, boolean strict, double ahp0, double ahp1, double ahp2, double ahp3, double ahp4,
			double ahp5, double ahp6, double ahp7, double ahp8, String ahpName, boolean mean, long seed, Map<String, String> dataHT) throws Exception {
		boolean machineReadable = false;

		System.out.println("----------Project & Decomp creation----------");
		MutablePair<String, File> projectFile = ProjectCreationDecompTask.run(name, folderIn, folderOut, xmin, ymin, width, height, shiftX, shiftY, dataHT, maxSize, minSize,
				seuilDensBuild, machineReadable);
		System.out.println("----------Simulation task----------");
		SimulTask.saveEval = false;
		File scenarFile = SimulTask.run(projectFile.getRight(), projectFile.getLeft(), nMax, strict, ahp0, ahp1, ahp2, ahp3, ahp4, ahp5, ahp6, ahp7, ahp8, ahpName, mean, seed,
				machineReadable);
		for (int i = 0; i < 99; i++) {
			seed = (long) (Math.random() * 100000);
			scenarFile = SimulTask.run(projectFile.getRight(), projectFile.getLeft(), nMax, strict, ahp0, ahp1, ahp2, ahp3, ahp4, ahp5, ahp6, ahp7, ahp8, ahpName, mean, seed,
					machineReadable);
			System.out.println("scenar file : " + scenarFile);

		}
		try {
			AnalyseTask.runStab(projectFile.getRight(), folderIn, name, machineReadable);
		} catch (Exception e) {
			System.out.println("ton système n'est pas assez stable mon cochon");
		}
		System.out.println("----------End task----------");
		return null;
	}
}
