package fr.ign.exp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.MutablePair;

import fr.ign.analyse.RasterAnalyse;
import fr.ign.task.AnalyseTask;
import fr.ign.task.ProjectCreationDecompTask;
import fr.ign.task.SimulTask;
import fr.ign.tools.DataSetSelec;
import fr.ign.tools.ScenarTools;

public class diffDataSources {
	public static void main(String[] args) throws Exception {
		String name = "DiffData";
		SimulTask.saveEvalAnal = true;
		SimulTask.saveWholeProj = true;

		File folderIn = new File("./donnee/all");
		File folderOut = new File("./result/sens/diffDataSource");
		List<List<File>> compNU = new ArrayList<List<File>>();

		DataSetSelec.predefSet();
		Map<String, String> dataHT = DataSetSelec.get("Data1");
		double width = 26590;
		double height = 26590;

		double shiftX = 0;
		double shiftY = 0;
		double xmin = 915948;
		double ymin = 6677337;

		double minSize = 20;
		double maxSize = 14580;
		// double maxSize = 200;
		double seuilDensBuild = 0;

		// setting on our six ahp objects
		HashMap<String, Double> toUse = new HashMap<String, Double>();
		HashMap<String, Double> ahpS_Moy = new HashMap<String, Double>();
		HashMap<String, Double> ahpS_Yag = new HashMap<String, Double>();

		ahpS_Yag.put("ahp8", 0.745);
		ahpS_Yag.put("ahp7", 0.745);
		ahpS_Yag.put("ahp6", 0.745);
		ahpS_Yag.put("ahp5", 0.359);
		ahpS_Yag.put("ahp4", 1.965);
		ahpS_Yag.put("ahp3", 1.965);
		ahpS_Yag.put("ahp2", 1.965);
		ahpS_Yag.put("ahp1", 0.269);
		ahpS_Yag.put("ahp0", 0.243);
		ahpS_Yag.put("Yag_ahpS", 99.0);

		ahpS_Moy.put("ahp8", 0.083);
		ahpS_Moy.put("ahp7", 0.083);
		ahpS_Moy.put("ahp6", 0.083);
		ahpS_Moy.put("ahp5", 0.04);
		ahpS_Moy.put("ahp4", 0.218);
		ahpS_Moy.put("ahp3", 0.218);
		ahpS_Moy.put("ahp2", 0.218);
		ahpS_Moy.put("ahp1", 0.03);
		ahpS_Moy.put("ahp0", 0.027);
		ahpS_Moy.put("Moy_ahpS", 99.0);

		// list of AHP to loop in
		List<HashMap<String, Double>> ahpList = new ArrayList<HashMap<String, Double>>();
		ahpList.add(ahpS_Moy);
		ahpList.add(ahpS_Yag);

		long seed = 42L;

		System.out.println("----------Simulation task----------");
		SimulTask.saveEval = true;

		// autom
		List<File> compNUManu = new ArrayList<>();
		List<File> compNUAutom = new ArrayList<>();
		MutablePair<String, File> projectFile = ProjectCreationDecompTask.run(name, folderIn, folderOut, xmin, ymin, width, height, shiftX, shiftY, dataHT, maxSize, minSize,
				seuilDensBuild, false);
		compNUManu.add(projectFile.getRight());
		toUse = ahpS_Moy;
		for (int j = 0; j <= 3; j++) {

			switch (j) {
			case 1:
				dataHT = DataSetSelec.get("Data1.1");
				projectFile = ProjectCreationDecompTask.run(name, folderIn, folderOut, xmin, ymin, width, height, shiftX, shiftY, dataHT, maxSize, minSize, seuilDensBuild, false);
				compNUManu.add(projectFile.getRight());
				break;
			//
			case 2:
				dataHT = DataSetSelec.get("Data2");
				projectFile = ProjectCreationDecompTask.run(name, folderIn, folderOut, xmin, ymin, width, height, shiftX, shiftY, dataHT, maxSize, minSize, seuilDensBuild, false);
				compNUAutom.add(projectFile.getRight());
				break;
			case 3:
				dataHT = DataSetSelec.get("Data2.1");
				projectFile = ProjectCreationDecompTask.run(name, folderIn, folderOut, xmin, ymin, width, height, shiftX, shiftY, dataHT, maxSize, minSize, seuilDensBuild, false);
				compNUAutom.add(projectFile.getRight());
				break;
			}

			for (int i = 0; i <= 3; i++) {

				int nMax = 4;
				boolean strict = true;
				boolean mean = true;
				toUse = ahpS_Moy;
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
					toUse = ahpS_Yag;
					break;
				}
				String ahpName = ScenarTools.getAHPName(toUse);
				SimulTask.run(projectFile.getRight(), projectFile.getLeft(), nMax, strict, toUse.get("ahp0"), toUse.get("ahp1"), toUse.get("ahp2"), toUse.get("ahp3"),
						toUse.get("ahp4"), toUse.get("ahp5"), toUse.get("ahp6"), toUse.get("ahp7"), toUse.get("ahp8"), ahpName, mean, seed, false);
			}
		}

		for (List<File> lF : compNU) {
			File specOut = new File(folderOut, lF.get(0).getName());
			RasterAnalyse.rootFile = specOut;
			RasterAnalyse.echelle = "20";
			for (int ech = 20; ech <= 180; ech = ech * 3) {
				AnalyseTask.compProjects(lF, "DiffData", specOut, String.valueOf(ech));
			}
		}
	}
}
