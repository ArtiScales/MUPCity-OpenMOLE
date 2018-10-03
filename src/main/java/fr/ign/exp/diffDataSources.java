package fr.ign.exp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.MutablePair;

import fr.ign.task.ProjectCreationDecompTask;
import fr.ign.task.SimulTask;
import fr.ign.tools.DataSetSelec;
import fr.ign.tools.ScenarTools;

public class diffDataSources {
	public static void main(String[] args) throws Exception {
	String name = "StabiliteTestAHP-Autom-CM20.0-S0.0-GP_915948.0_6677337.0";
	SimulTask.saveEvalAnal = true;
	SimulTask.saveWholeProj = true;

	File folderIn = new File("./stabilite/");
	File folderOut = new File("./result/sens/diffDataSource");
	Map<String, String> dataHT = DataSetSelec.get("Data1");
	double width = 26590;
	double height = 26590;

	double shiftX = 0;
	double shiftY = 0;
	double xmin = 915948;
	double ymin = 6677337;

	double minSize = 15;
	double maxSize = 14580;
	// double maxSize = 200;
	double seuilDensBuild = 0;
	
	// setting on our six ahp objects
	HashMap<String, Double> toUse = new HashMap<String, Double>();
	HashMap<String, Double> ahpE_Moy = new HashMap<String, Double>();
	HashMap<String, Double> ahpS_Moy = new HashMap<String, Double>();
	HashMap<String, Double> ahpT_Moy = new HashMap<String, Double>();
	HashMap<String, Double> ahpE_Yag = new HashMap<String, Double>();// creation of the true objects
	HashMap<String, Double> ahpT_Yag = new HashMap<String, Double>();
	HashMap<String, Double> ahpS_Yag = new HashMap<String, Double>();

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

	long seed = 42L;

	System.out.println("----------Simulation task----------");
	SimulTask.saveEval = true;

	//autom
	MutablePair<String, File> projectFile = ProjectCreationDecompTask.run(name, folderIn, folderOut, xmin, ymin,
			width, height, shiftX, shiftY, dataHT, maxSize, minSize, seuilDensBuild, false);
	for (int j = 0; j <= 3; j++) {
		switch (j) {
		
		case 1:
			dataHT = DataSetSelec.get("Data1.1");
			projectFile = ProjectCreationDecompTask.run(name, folderIn, folderOut, xmin, ymin,
					width, height, shiftX, shiftY, dataHT, maxSize, minSize, seuilDensBuild, false);
			break;
		//
		case 2:
			dataHT = DataSetSelec.get("Data2");
			projectFile = ProjectCreationDecompTask.run(name, folderIn, folderOut, xmin, ymin,
					width, height, shiftX, shiftY, dataHT, maxSize, minSize, seuilDensBuild, false);
			break;
		case 3:
			dataHT = DataSetSelec.get("Data2.2");
			projectFile = ProjectCreationDecompTask.run(name, folderIn, folderOut, xmin, ymin,
					width, height, shiftX, shiftY, dataHT, maxSize, minSize, seuilDensBuild, false);
			break;

		}
		for (int i = 0; i <= 3; i++) {

			toUse = ahpE_Moy;

			int nMax = 4;
			boolean strict = true;
			boolean mean = true;
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
			SimulTask.run(projectFile.getRight(), projectFile.getLeft(), nMax, strict, toUse.get("ahp0"),
					toUse.get("ahp1"), toUse.get("ahp2"), toUse.get("ahp3"), toUse.get("ahp4"), toUse.get("ahp5"),
					toUse.get("ahp6"), toUse.get("ahp7"), toUse.get("ahp8"), ahpName, mean, seed, false);

		}
	}
	}
}
