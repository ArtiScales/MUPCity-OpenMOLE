package fr.ign.task;

import java.io.File;
import java.util.Map;

import org.apache.commons.lang3.tuple.MutablePair;

import fr.ign.analyse.FractalDimention;
import fr.ign.exp.DataSetSelec;

public class CompositeTask {
	public static void main(String[] args) throws Exception {

		DataSetSelec.predefSet();
		Map<String, String> dataHT = DataSetSelec.get("Data1");
		String name = "Stabilite-evals";
		File folderIn = new File("/media/mcolomb/Data_2/dataOpenMole/stabilite/dataManu");
		File folderOut = new File("/media/mcolomb/Data_2/resultFinal/stab/evals");
		File discreteFile = new File("/home/mcolomb/informatique/MUP/explo/dataExtra/admin_typo.shp");
		File buildFile = new File("/home/mcolomb/donnee/couplage/donneeGeographiques/batiment.shp");
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

		int nMax = 5;
		boolean strict = false;

		// double ahp8 = 0.083;
		// double ahp7 = 0.083;
		// double ahp6 = 0.083;
		// double ahp5 = 0.04;
		// double ahp4 = 0.218;
		// double ahp3 = 0.218;
		// double ahp2 = 0.218;
		// double ahp1 = 0.03;
		// double ahp0 = 0.027;

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
		long seed = 381756331518069474L;
		File[] file = new File[3];
		File[] fileDonnee = { folderIn };
		String[] names = { name };


			file[1] = run(name, folderIn, folderOut, discreteFile, buildFile, xmin, ymin, width, height, shiftX, shiftY, minSize, maxSize, seuilDensBuild, nMax, strict, ahp0, ahp1,
					ahp2, ahp3, ahp4, ahp5, ahp6, ahp7, ahp8, mean, seed, dataHT);
		
		folderIn = new File("/media/mcolomb/Data_2/dataOpenMole/stabilite/dataAutom");
		dataHT = DataSetSelec.get("Data2");

			file[2] = run(name, folderIn, folderOut, discreteFile, buildFile, xmin, ymin, width, height, shiftX, shiftY, minSize, maxSize, seuilDensBuild, nMax, strict, ahp0, ahp1,
					ahp2, ahp3, ahp4, ahp5, ahp6, ahp7, ahp8, mean, seed, dataHT);
		
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
		for (int i = 0; i <= 100; i++) {
			seed = (long) (Math.random() * 100000);
		File scenarFile = SimulTask.run(projectFile.getRight(), projectFile.getLeft(), nMax, strict, ahp0, ahp1, ahp2, ahp3, ahp4, ahp5, ahp6, ahp7, ahp8, mean, seed,
				machineReadable);
		System.out.println("scenar file : " + scenarFile);
		}
		System.out.println("----------End task----------");
		return null;
	}
}
