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

public class AHPExplore {

	public static void main(String[] args) throws Exception {

		DataSetSelec.predefSet();
		Map<String, String> dataHT = DataSetSelec.get("Data1");
		String name = "StabiliteTestAHP";
		File folderIn = new File("./stabilite/dataManu");
		File folderOut = new File("./result/testAHP/2emevague");
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

		// setting on our six ahp objects
		HashMap<String, Double> ahpE_Yag = new HashMap<String, Double>();// creation of the true objects
		HashMap<String, Double> ahpT_Yag = new HashMap<String, Double>();
		HashMap<String, Double> ahpS_Yag = new HashMap<String, Double>();
		HashMap<String, Double> ahpE_Moy = new HashMap<String, Double>();
		HashMap<String, Double> ahpS_Moy = new HashMap<String, Double>();
		HashMap<String, Double> ahpT_Moy = new HashMap<String, Double>();

		// HashMap<String, Double> coefsE_Yag = new HashMap<>();// collection of the eigenvector values
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
		ahpList.add(ahpE_Yag);
		ahpList.add(ahpT_Yag);
		ahpList.add(ahpS_Yag);
		ahpList.add(ahpE_Moy);
		ahpList.add(ahpT_Moy);
		ahpList.add(ahpS_Moy);

		long seed = 42L;

		boolean strict = false;

		MutablePair<String, File> projectFile = ProjectCreationDecompTask.run(name, folderIn, folderOut, xmin, ymin, width, height, shiftX, shiftY, dataHT, maxSize, minSize,
				seuilDensBuild, false);
		System.out.println("----------Simulation task----------");
		SimulTask.saveEval = false;
		for (int g = 0; g < 1; g++) {
			int nMax = 4;
			strict = true;
			for (HashMap<String, Double> list : ahpList) {
				String ahpName = ScenarTools.getAHPName(list);

				// scénario séparé pour avoir une seed répliqué (de 42)
				SimulTask.run(projectFile.getRight(), projectFile.getLeft(), nMax, strict, list.get("ahp0"), list.get("ahp1"), list.get("ahp2"), list.get("ahp3"), list.get("ahp4"),
						list.get("ahp5"), list.get("ahp6"), list.get("ahp7"), list.get("ahp8"), ahpName, ScenarTools.setAgregMethod(list), seed, false);

				for (int ii = 0; ii <= 98; ii++) {
					seed = (long) (Math.random() * 1000);
					SimulTask.run(projectFile.getRight(), projectFile.getLeft(), nMax, strict, list.get("ahp0"), list.get("ahp1"), list.get("ahp2"), list.get("ahp3"),
							list.get("ahp4"), list.get("ahp5"), list.get("ahp6"), list.get("ahp7"), list.get("ahp8"), ahpName, ScenarTools.setAgregMethod(list), seed, false);
				}
			}
		}

		// analyse the result
		RasterAnalyse.echelle = "20";
		RasterAnalyse.statFile = new File("/media/mcolomb/Data_2/resultFinal/testAHP/troisiemeVague/stat");
		AnalyseTask.runStabAHP(folderOut, new File("/media/mcolomb/Data_2/dataOpenMole/stabilite/dataManu"), "StabiliteTestAHP", false);

	}

}
