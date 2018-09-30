package fr.ign.exp;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.MutablePair;

import fr.ign.task.ProjectCreationDecompTask;
import fr.ign.task.SimulTask;
import fr.ign.tools.DataSetSelec;
import fr.ign.tools.ScenarTools;

public class mvGrdi {

	public static void main(String[] args) throws Exception {
		DataSetSelec.predefSet();
		Map<String, String> dataHT = DataSetSelec.get("Data1");
		String name = "GridMouv";
		File folderIn = new File("./stabilite/dataManu");
		File folderOut = new File("./result/sens/GridMouv");
		File discreteFile = new File(folderIn, "admin_typo.shp");
		File buildFile = new File(folderIn, "batimentPro.shp");
		double width = 26590;
		double height = 26590;

		double shiftX = 0;
		double shiftY = 0;

		double minSize = 15;
		double maxSize = 14580;
		// double maxSize = 200;
		double seuilDensBuild = 0;

		// setting on our six ahp objects
		HashMap<String, Double> ahpE_Moy = new HashMap<String, Double>();

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

		String ahpName = ScenarTools.getAHPName(ahpE_Moy);
		long seed = 42L;

		System.out.println("----------Simulation task----------");
		SimulTask.saveEval = false;

		for (minSize = 10; minSize < 20; minSize++) {
			for (int xSlide = -1; xSlide <= 1; xSlide++) {
				for (int ySlide = -1; ySlide <= 1; ySlide++) {
					double xmin = 915948 + xSlide * minSize;
					double ymin = 6677337 + ySlide * minSize;

					MutablePair<String, File> projectFile = ProjectCreationDecompTask.run(name, folderIn, folderOut, xmin, ymin, width, height, shiftX, shiftY, dataHT, maxSize,
							minSize, seuilDensBuild, false);
					for (int g = 0; g < 2; g++) {
						int nMax = 4;
						boolean strict = true;
						switch (g) {
						case 1:
							nMax = 5;
							strict = false;
							break;
						case 2:
							nMax = 6;
							strict = true;
							break;
						}

						File fileScenar = SimulTask.run(projectFile.getRight(), projectFile.getLeft(), nMax, strict, ahpE_Moy.get("ahp0"), ahpE_Moy.get("ahp1"),
								ahpE_Moy.get("ahp2"), ahpE_Moy.get("ahp3"), ahpE_Moy.get("ahp4"), ahpE_Moy.get("ahp5"), ahpE_Moy.get("ahp6"), ahpE_Moy.get("ahp7"),
								ahpE_Moy.get("ahp8"), ahpName, true, seed, false);

					}
				}
			}
		}
	}
}
