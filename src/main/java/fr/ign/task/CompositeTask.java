package fr.ign.task;

import java.io.File;
import java.util.Map;

import fr.ign.exp.DataSetSelec;

public class CompositeTask {
	public static void main(String[] args) throws Exception {

		DataSetSelec.predefSet();
		Map<String, String> dataHT = DataSetSelec.get("Data1.0");

		String name = "emprise";
		File folderIn = new File("./data/");
		File folderOut = new File("./result/emprise");
		File discreteFile = new File("/home/mcolomb/informatique/MUP/explo/dataExtra/admin_typo.shp");
		File buildFile = new File("/home/mcolomb/donnee/couplage/donneeGeographiques/batiment.shp");
		double width = 26590/20;
		double height = 26590/20;
		double xmin = 915948;
		double ymin = 6677337;
		double shiftX = 0;
		double shiftY = 0;

		double minSize = 20;
		double maxSize = 1620;
		double seuilDensBuild = 0;

		int nMax = 5;
		boolean strict = true;

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
		long seed = 42;
		File filout = run(name, folderIn, folderOut, discreteFile, buildFile, xmin, ymin, width, height, shiftX, shiftY, minSize, maxSize, seuilDensBuild, nMax, strict, ahp0,
				ahp1, ahp2, ahp3, ahp4, ahp5, ahp6, ahp7, ahp8, mean, seed, dataHT);
		System.out.println(filout);
	}

	public static File run(String name, File folderIn, File folderOut, File discreteFile, File buildFile, double xmin, double ymin, double width, double height, double shiftX,
			double shiftY, double minSize, double maxSize, double seuilDensBuild, int nMax, boolean strict, double ahp0, double ahp1, double ahp2, double ahp3, double ahp4,
			double ahp5, double ahp6, double ahp7, double ahp8, boolean mean, long seed) throws Exception {

		return run(name, folderIn, folderOut, discreteFile, buildFile, xmin, ymin, width, height, shiftX, shiftY, minSize, maxSize, seuilDensBuild, nMax, strict, ahp0, ahp1, ahp2,
				ahp3, ahp4, ahp5, ahp6, ahp7, ahp8, mean, seed, DataSetSelec.dig(folderIn));

	}

	public static File run(String name, File folderIn, File folderOut, File discreteFile, File buildFile, double xmin, double ymin, double width, double height, double shiftX,
			double shiftY, double minSize, double maxSize, double seuilDensBuild, int nMax, boolean strict, double ahp0, double ahp1, double ahp2, double ahp3, double ahp4,
			double ahp5, double ahp6, double ahp7, double ahp8, boolean mean, long seed, Map<String, String> dataHT) throws Exception {
		System.out.println("----------Project creation----------");
		File projectFile = ProjectCreationTask.run(name, folderIn, folderOut, xmin, ymin, width, height, shiftX, shiftY, dataHT);
		System.out.println("----------Decomp task----------");
		DecompTask.run(projectFile, ProjectCreationTask.getName(), minSize, maxSize, seuilDensBuild);
		System.out.println("----------Simulation task----------");
		for (long seedCh = 42; seedCh < 1042; seedCh = seedCh + 1) {
			SimulTask.run(projectFile, ProjectCreationTask.getName(), nMax, strict, ahp0, ahp1, ahp2, ahp3, ahp4, ahp5, ahp6, ahp7, ahp8, mean, seedCh);
		}
		File resultFile = RasterAnalyseTask.runStab(projectFile, discreteFile, buildFile, SimulTask.getName());
		System.out.println("----------End task----------");
		return resultFile;
	}
}
