package fr.ign.exp;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.MutablePair;

import fr.ign.task.ProjectCreationDecompTask;
import fr.ign.task.SimulTask;
import fr.ign.tools.DataSetSelec;
import fr.ign.tools.ScenarTools;

public class mouvData {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		DataSetSelec.predefSet();
		Map<String, String> dataHT = DataSetSelec.get("Manu");
		String name = "dataMouv";
		File folderIn = new File("./donnee/stabilite/dataManu");
		File folderOut = new File("./result/sens/dataMouv");
		double width = 26590;
		double height = 26590;
		double xmin = 915948;
		double ymin = 6677337;

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
		int indice = 0;
		for (int xSlide = -1; xSlide <= 1; xSlide++) {
			for (int ySlide = -1; ySlide <= 1; ySlide++) {
				//change the name to avoid overwritting (translation is not in the project's name)
				String newName = name + indice;
				MutablePair<String, File> projectFile = ProjectCreationDecompTask.run(newName, folderIn, folderOut, xmin, ymin, width, height, xSlide, ySlide, dataHT, maxSize,
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
				indice++;
			}
		}
	}
}
