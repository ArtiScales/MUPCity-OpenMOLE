package fr.ign.task;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.MutablePair;

import fr.ign.tools.DataSetSelec;
import fr.ign.tools.ScenarTools;

public class CompositeTask {
	public static void main(String[] args) throws Exception {

		DataSetSelec.predefSet();
		Map<String, String> dataHT = DataSetSelec.get("Manu");
		String name = "newDimFract";
		File folderIn = new File("/media/mcolomb/Data_2/dataOpenMole/stabilite/dataManu");
		File folderOut = new File("/home/mcolomb/lastDimFract/out/");
//		File discreteFile = new File("/media/mcolomb/Data_2/dataOpenMole/stabilite/dataManu/discreteFile.shp");
//		File buildFile = new File("/media/mcolomb/Data_2/dataOpenMole/stabilite/dataManu/batimentPro.shp");
		double width = 26590;
		double height = 26590;
		// double width = 200;
		// double height = 200;
		double xmin = 915948;
		double ymin = 6677337;
		double shiftX = 0;
		double shiftY = 0;

		double buildDensityThreshold = 0;

		double minSize = 20;
		double maxSize = 14580;
		// double maxSize = 200;
//		double seuilDensBuild = 0;

		HashMap<String, Double> ahpT_Yag = new HashMap<String, Double>();
		HashMap<String, Double> ahpT_Moy = new HashMap<String, Double>();
		HashMap<String, Double> toUse = new HashMap<String, Double>();

		ahpT_Moy.put("ahp8", 0.051);
		ahpT_Moy.put("ahp7", 0.051);
		ahpT_Moy.put("ahp6", 0.051);
		ahpT_Moy.put("ahp5", 0.403);
		ahpT_Moy.put("ahp4", 0.133);
		ahpT_Moy.put("ahp3", 0.133);
		ahpT_Moy.put("ahp2", 0.133);
		ahpT_Moy.put("ahp1", 0.022);
		ahpT_Moy.put("ahp0", 0.022);
		ahpT_Moy.put("Moy_ahpT", 99.0);

		ahpT_Yag.put("ahp8", 0.458);
		ahpT_Yag.put("ahp7", 0.458);
		ahpT_Yag.put("ahp6", 0.458);
		ahpT_Yag.put("ahp5", 3.625);
		ahpT_Yag.put("ahp4", 1.199);
		ahpT_Yag.put("ahp3", 1.199);
		ahpT_Yag.put("ahp2", 1.199);
		ahpT_Yag.put("ahp1", 0.202);
		ahpT_Yag.put("ahp0", 0.202);
		ahpT_Yag.put("Yag_ahpT", 99.0);

		long seed = 42L;

		MutablePair<String, File> projectFile = ProjectCreationDecompTask.run(name, folderIn, folderOut, xmin, ymin, width, height, shiftX, shiftY, dataHT, maxSize, minSize,
				buildDensityThreshold, false);
		for (int i = 0; i <= 3; i++) {
			int nMax = 4;
			boolean strict = true;
			boolean mean = true;
			toUse = ahpT_Moy;
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
				toUse = ahpT_Yag;
				break;
			}
			String ahpName = ScenarTools.getAHPName(toUse);

			SimulTask.run(projectFile.getRight(), projectFile.getLeft(), nMax, strict, toUse.get("ahp0"), toUse.get("ahp1"), toUse.get("ahp2"), toUse.get("ahp3"),
					toUse.get("ahp4"), toUse.get("ahp5"), toUse.get("ahp6"), toUse.get("ahp7"), toUse.get("ahp8"), ahpName, mean, seed, false);

		}

	}

	/**
	 * Method used if no dataSet is provided. The folderIn is dug to find matches with the wanted format
	 * 
	 * @throws Exception
	 */
	public static File run(String name, File folderIn, File folderOut, File discreteFile, File buildFile, double xmin, double ymin, double width, double height, double shiftX,
			double shiftY, double minSize, double maxSize, double seuilDensBuild, int nMax, boolean strict, double ahp0, double ahp1, double ahp2, double ahp3, double ahp4,
			double ahp5, double ahp6, double ahp7, double ahp8, boolean mean, long seed) throws Exception {

		return run(name, folderIn, folderOut, discreteFile, buildFile, xmin, ymin, width, height, shiftX, shiftY, minSize, maxSize, seuilDensBuild, nMax, strict, ahp0, ahp1, ahp2,
				ahp3, ahp4, ahp5, ahp6, ahp7, ahp8, mean, seed, DataSetSelec.dig(folderIn));
	}

	public static File run(String name, File folderIn, File folderOut, File discreteFile, File buildFile, double xmin, double ymin, double width, double height, double shiftX,
			double shiftY, double minSize, double maxSize, double seuilDensBuild, int nMax, boolean strict, double ahp0, double ahp1, double ahp2, double ahp3, double ahp4,
			double ahp5, double ahp6, double ahp7, double ahp8, boolean mean, long seed, Map<String, String> dataHT) throws Exception {
		boolean machineReadable = true;

		System.out.println("----------Project & Decomp creation----------");
		MutablePair<String, File> projectFile = ProjectCreationDecompTask.run(name, folderIn, folderOut, xmin, ymin, width, height, shiftX, shiftY, dataHT, maxSize, minSize,
				seuilDensBuild, machineReadable);
		System.out.println("----------Simulation task----------");
		SimulTask.saveWholeProj = true;
		File scenarFile = SimulTask.run(projectFile.getRight(), projectFile.getLeft(), nMax, strict, ahp0, ahp1, ahp2, ahp3, ahp4, ahp5, ahp6, ahp7, ahp8, mean, seed,
				machineReadable);

		scenarFile = SimulTask.run(projectFile.getRight(), projectFile.getLeft(), nMax, strict, ahp0, ahp1, ahp2, ahp3, ahp4, ahp5, ahp6, ahp7, ahp8, mean, seed, machineReadable);
		System.out.println("scenar file : " + scenarFile);

		System.out.println("----------End task----------");
		return null;
	}
}
