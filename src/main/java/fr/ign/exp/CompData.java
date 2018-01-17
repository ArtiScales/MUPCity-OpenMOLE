package fr.ign.exp;

import java.io.File;
import java.util.Map;

import fr.ign.task.DecompTask;
import fr.ign.task.ProjectCreationTask;
import fr.ign.task.RasterAnalyseTask;
import fr.ign.task.SimulTask;

public class CompData {

	public static void main(String[] args) throws Exception {

		File rootFile = new File("/media/mcolomb/Data_2/dataOpenMole/compDonnee");
		for (File folderIn : rootFile.listFiles()) {
			Map<String, String> dataHT = DataSetSelec.dig(folderIn);
			String name = dataHT.get("name");
			File folderOut = new File("./result/compDonnee/", folderIn.getName());
			File discreteFile = new File(folderIn, "/admin_typo.shp");
			File buildFile = new File(folderIn, dataHT.get("build"));
			double width = 28303;
			double height = 21019;
			double xmin = 914760;
			double ymin = 6680157;
			double shiftX = 0;
			double shiftY = 0;

			double minSize = 20;
			double maxSize = 5000;
			double seuilDensBuild = 0;
			int nMax = 5;
			boolean strict = true;

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
			run(name, folderIn, folderOut, discreteFile, buildFile, xmin, ymin, width, height, shiftX, shiftY, minSize, maxSize, seuilDensBuild, nMax, strict, ahp0, ahp1, ahp2,
					ahp3, ahp4, ahp5, ahp6, ahp7, ahp8, mean, seed, dataHT);
		}

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
		DecompTask.run(projectFile, name, minSize, maxSize, seuilDensBuild);
		System.out.println("----------Simulation task----------");

		SimulTask.saveWholeProj = true;
		SimulTask.run(projectFile, name, nMax, strict, ahp0, ahp1, ahp2, ahp3, ahp4, ahp5, ahp6, ahp7, ahp8, mean, seed);
		strict = false;
		SimulTask.run(projectFile, name, nMax, strict, ahp0, ahp1, ahp2, ahp3, ahp4, ahp5, ahp6, ahp7, ahp8, mean, seed);
		nMax = 6;
		strict = true;
		SimulTask.run(projectFile, name, nMax, strict, ahp0, ahp1, ahp2, ahp3, ahp4, ahp5, ahp6, ahp7, ahp8, mean, seed);

		strict = false;
		ahp8 = 1;
		ahp7 = 1;
		ahp6 = 1;
		ahp5 = 1;
		ahp4 = 1;
		ahp3 = 1;
		ahp2 = 1;
		ahp1 = 1;
		ahp0 = 1;
		mean = false;
		SimulTask.run(projectFile, name, nMax, strict, ahp0, ahp1, ahp2, ahp3, ahp4, ahp5, ahp6, ahp7, ahp8, mean, seed);

		File resultFile = RasterAnalyseTask.runCompData(projectFile, buildFile, name);
		System.out.println("----------End task----------");
		return resultFile;
	}

}
