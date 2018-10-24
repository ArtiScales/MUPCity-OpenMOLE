package fr.ign.exp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.MutablePair;

import fr.ign.cogit.GTFunctions.Rasters;
import fr.ign.task.AnalyseTask;
import fr.ign.task.ProjectCreationDecompTask;
import fr.ign.task.SimulTask;
import fr.ign.tools.DataSetSelec;
import fr.ign.tools.ScenarTools;

public class mouvGrid {

	public static void main(String[] args) throws Exception {

		DataSetSelec.predefSet();
		Map<String, String> dataHT = DataSetSelec.get("Data1");
		String name = "GridMouv";
		File folderIn = new File("./stabilite/dataManu");
		File folderOut = new File("./result/sens/GridMouv");
		double width = 26590;
		double height = 26590;

		double shiftX = 0;
		double shiftY = 0;

		double minSize = 20;
		double maxSize = 14580;
		// double maxSize = 200;
		double seuilDensBuild = 0;

		// setting on our six ahp objects
		HashMap<String, Double> ahpE_Moy = new HashMap<String, Double>();
		HashMap<String, Double> toUse = new HashMap<String, Double>();
		HashMap<String, Double> ahpE_Yag = new HashMap<String, Double>();

		ahpE_Moy.put("ahp8", 0.111);
		ahpE_Moy.put("ahp7", 0.111);
		ahpE_Moy.put("ahp6", 0.111);
		ahpE_Moy.put("ahp5", 0.111);
		ahpE_Moy.put("ahp4", 0.111);
		ahpE_Moy.put("ahp3", 0.111);
		ahpE_Moy.put("ahp2", 0.111);
		ahpE_Moy.put("ahp1", 0.111);
		ahpE_Moy.put("ahp0", 0.111);
		ahpE_Moy.put("Moy_ahpE", 99.0);

		ahpE_Yag.put("ahp8", 1.0);
		ahpE_Yag.put("ahp7", 1.0);
		ahpE_Yag.put("ahp6", 1.0);
		ahpE_Yag.put("ahp5", 1.0);
		ahpE_Yag.put("ahp4", 1.0);
		ahpE_Yag.put("ahp3", 1.0);
		ahpE_Yag.put("ahp2", 1.0);
		ahpE_Yag.put("ahp1", 1.0);
		ahpE_Yag.put("ahp0", 1.0);
		ahpE_Yag.put("Yag_ahpE", 99.0);

		long seed = 42L;

		System.out.println("----------Simulation task----------");
		SimulTask.saveEval = false;
		SimulTask.saveWholeProj = true;

		for (int xSlide = -1; xSlide <= 1; xSlide++) {
			for (int ySlide = -1; ySlide <= 1; ySlide++) {
				double xmin = 915948 + xSlide * minSize;
				double ymin = 6677337 + ySlide * minSize;

				MutablePair<String, File> projectFile = ProjectCreationDecompTask.run(name, folderIn, folderOut, xmin, ymin, width, height, shiftX, shiftY, dataHT, maxSize,
						minSize, seuilDensBuild, false);

				toUse = ahpE_Moy;
				int nMax = 4;
				boolean strict = true;
				boolean mean = true;
				for (int i = 0; i <= 3; i++) {
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

					SimulTask.run(projectFile.getRight(), projectFile.getLeft(), nMax, strict, toUse.get("ahp0"), toUse.get("ahp1"), toUse.get("ahp2"), toUse.get("ahp3"),
							toUse.get("ahp4"), toUse.get("ahp5"), toUse.get("ahp6"), toUse.get("ahp7"), toUse.get("ahp8"), ahpName, mean, seed, false);

				}
			}
		}
		AnalyseTask.runGridExplo(folderOut, folderIn, String.valueOf(minSize), name, false);
	}

	public static void renameFiles() {
		File base = new File("/media/mcolomb/Data_2/resultFinal/sens/GridMouv");
		for (File f : base.listFiles()) {
			if (f.getName().startsWith("Grid")) {
				for (File ff : f.listFiles()) {
					if (ff.getName().startsWith("N")) {
						String[] chaine = ff.getName().split("_");
						String rename = chaine[0] + "_" + chaine[1] + "_" + chaine[3] + "_" + chaine[2].replace("Yag", "").replace("Moy", "") + "_" + chaine[4] + "_" + chaine[5];
						for (File fff : ff.listFiles()) {
							if (fff.getName().startsWith("N")) {
								String[] chaine2 = fff.getName().split("-");
								String rename2 = rename + "-" + chaine2[1] + "-" + chaine2[2];
								fff.renameTo(new File(fff.getParentFile(), rename2));
							}
						}
						ff.renameTo(new File(ff.getParent(), rename));
					}

				}
			}
		}
	}

	public static void getFileEval() throws IOException {
		File base = new File("/media/mcolomb/Data_2/resultFinal/sens/GridMouv");

		for (int n = 4; n <= 7; n++) {
			int i = 1;
			for (File f : base.listFiles()) {
				if (f.getName().startsWith("Grid")) {
					for (File ff : f.listFiles()) {
						if (ff.getName().startsWith("N")) {
							for (File fff : ff.listFiles()) {
								if (fff.getName().startsWith("N" + n) && fff.getName().endsWith("evalAnal-20.0.tif")) {
									OutputStream out = new FileOutputStream(
											new File("/media/mcolomb/Data_2/resultFinal/sens/GridMouv/result--GridMouv/evalComp", "N" + n + "/" + fff.getName() + "-" + i));
									// vu que c'est un .getPath sur un truc qui à pas vocation à être exporté, je suis pas sur que ça marche
									Files.copy(fff.toPath(), out);
									i++;
								}
							}
						}

					}

				}
			}
		}
	}

}
