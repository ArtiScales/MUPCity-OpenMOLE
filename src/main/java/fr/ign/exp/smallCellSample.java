package fr.ign.exp;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.MutablePair;

import fr.ign.task.ProjectCreationDecompTask;
import fr.ign.task.SimulTask;
import fr.ign.tools.DataSetSelec;

public class smallCellSample {

	public static void main(String[] args) throws Exception {

		DataSetSelec.predefSet();
		Map<String, String> dataHT = DataSetSelec.get("Data1");
		String name = "Sensibilite-testCM";
		File folderIn = new File("./stabilite/dataManu");
		File folderOut = new File("./resultFinal/sens/cellsizesample");
		File discreteFile = new File(folderIn, "admin_typo.shp");
		File buildFile = new File(folderIn, "batimentPro.shp");
//		double width = 26590;
//		double height = 26590;
		 double width = 200;
		 double height = 200;
		double xmin = 915948;
		double ymin = 6677337;
		double shiftX = 0;
		double shiftY = 0;

		double minSize = 10;
		double maxSize = 14580;
		// double maxSize = 200;
		double seuilDensBuild = 0;

		double ahp8 = 0.111;
		double ahp7 = 0.111;
		double ahp6 = 0.111;
		double ahp5 = 0.111;
		double ahp4 = 0.111;
		double ahp3 = 0.111;
		double ahp2 = 0.111;
		double ahp1 = 0.111;
		double ahp0 = 0.111;
		boolean mean = true;
		long seed = 1116L;
		int nMax = 5;
		boolean strict = false;

		run(name, folderIn, folderOut, discreteFile, buildFile, xmin, ymin, width, height, shiftX, shiftY, minSize, maxSize, seuilDensBuild, nMax, strict, ahp0, ahp1, ahp2, ahp3,
				ahp4, ahp5, ahp6, ahp7, ahp8, mean, seed, dataHT);

		nMax = 6;
		strict = true;

		run(name, folderIn, folderOut, discreteFile, buildFile, xmin, ymin, width, height, shiftX, shiftY, minSize, maxSize, seuilDensBuild, nMax, strict, ahp0, ahp1, ahp2, ahp3,
				ahp4, ahp5, ahp6, ahp7, ahp8, mean, seed, dataHT);

		minSize = 15;

		nMax = 5;
		strict = false;

		run(name, folderIn, folderOut, discreteFile, buildFile, xmin, ymin, width, height, shiftX, shiftY, minSize, maxSize, seuilDensBuild, nMax, strict, ahp0, ahp1, ahp2, ahp3,
				ahp4, ahp5, ahp6, ahp7, ahp8, mean, seed, dataHT);

		nMax = 6;
		strict = true;

		run(name, folderIn, folderOut, discreteFile, buildFile, xmin, ymin, width, height, shiftX, shiftY, minSize, maxSize, seuilDensBuild, nMax, strict, ahp0, ahp1, ahp2, ahp3,
				ahp4, ahp5, ahp6, ahp7, ahp8, mean, seed, dataHT);

		folderIn = new File("./stabilite/dataAutom");

		dataHT = DataSetSelec.get("Data2");
		minSize = 10;

		nMax = 5;
		strict = false;

		run(name, folderIn, folderOut, discreteFile, buildFile, xmin, ymin, width, height, shiftX, shiftY, minSize, maxSize, seuilDensBuild, nMax, strict, ahp0, ahp1, ahp2, ahp3,
				ahp4, ahp5, ahp6, ahp7, ahp8, mean, seed, dataHT);

		nMax = 6;
		strict = true;

		run(name, folderIn, folderOut, discreteFile, buildFile, xmin, ymin, width, height, shiftX, shiftY, minSize, maxSize, seuilDensBuild, nMax, strict, ahp0, ahp1, ahp2, ahp3,
				ahp4, ahp5, ahp6, ahp7, ahp8, mean, seed, dataHT);
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

		seed = (long) (Math.random() * 100000);
		scenarFile = SimulTask.run(projectFile.getRight(), projectFile.getLeft(), nMax, strict, ahp0, ahp1, ahp2, ahp3, ahp4, ahp5, ahp6, ahp7, ahp8, mean, seed, machineReadable);
		System.out.println("scenar file : " + scenarFile);

		System.out.println("----------End task----------");
		return null;
	}

}
