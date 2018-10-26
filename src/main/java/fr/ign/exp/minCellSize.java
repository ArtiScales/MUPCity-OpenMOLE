package fr.ign.exp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.MutablePair;
import org.opengis.parameter.InvalidParameterValueException;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.referencing.operation.TransformException;

import fr.ign.cogit.GTFunctions.Rasters;
import fr.ign.task.AnalyseTask;
import fr.ign.task.ProjectCreationDecompTask;
import fr.ign.task.SimulTask;
import fr.ign.tools.DataSetSelec;
import fr.ign.tools.ScenarTools;

public class minCellSize {

	public static void main(String[] args) throws Exception {

		String name = "cellSize";
		SimulTask.saveEvalAnal = true;
		SimulTask.saveWholeProj = true;

		File folderIn = new File("./donnee/all/");
		File folderOut = new File("./result/sens/cellSize");
		DataSetSelec.predefSet();
		Map<String, String> dataHT = DataSetSelec.get("Data1");
		double width = 26590;
		double height = 26590;

		double shiftX = 0;
		double shiftY = 0;
		double xmin = 915948;
		double ymin = 6677337;
		double buildDensityThreshold = 0;
		double maxSize = 14580;

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

		// list of matrices to loop in
		List<HashMap<String, Double>> ahpList = new ArrayList<HashMap<String, Double>>();
		ahpList.add(ahpS_Moy);
		ahpList.add(ahpS_Yag);

		long seed = 42L;

		System.out.println("----------Simulation task----------");
		SimulTask.saveEval = true;

		// autom

		toUse = ahpS_Moy;
		for (double minSize = 20; minSize >= 10; minSize--) {

			MutablePair<String, File> projectFile = ProjectCreationDecompTask.run(name, folderIn, folderOut, xmin, ymin, width, height, shiftX, shiftY, dataHT, maxSize, minSize,
					buildDensityThreshold, false);
			File fIn = new File("/media/mcolomb/Data_2/resultFinal/sens/cellSize");
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
				for (int j = 0; j < 9; j++) {
					seed = (long) (Math.random() * 1000);
					SimulTask.run(projectFile.getRight(), projectFile.getLeft(), nMax, strict, toUse.get("ahp0"), toUse.get("ahp1"), toUse.get("ahp2"), toUse.get("ahp3"),
							toUse.get("ahp4"), toUse.get("ahp5"), toUse.get("ahp6"), toUse.get("ahp7"), toUse.get("ahp8"), ahpName, mean, seed, false);
				}
			}
		}
		AnalyseTask.runSizeCellExplo(folderOut, folderIn,null, name, false);
	}

	/**
	 * make the same emprise for every simulation
	 * 
	 * @param mainFile
	 * @param name
	 * @throws IOException
	 * @throws InvalidParameterValueException
	 * @throws ParameterNotFoundException
	 * @throws TransformException
	 */
	public static void cutOut(File mainFile, String name, File empriseFile) throws IOException, InvalidParameterValueException, ParameterNotFoundException, TransformException {
		File cutedFolder = new File(mainFile, "cuted");
		for (File projectFile : mainFile.listFiles()) {
			if (projectFile.getName().startsWith(name)) {
				File cutProject = new File(cutedFolder, projectFile.getName());
				for (File scenarFile : projectFile.listFiles()) {
					if (scenarFile.getName().startsWith("N")) {
						File cutScenar = new File(cutProject, scenarFile.getName());
						cutScenar.mkdirs();
						for (File fileFile : scenarFile.listFiles()) {
							if (fileFile.getName().contains("evalAnal") && fileFile.getName().endsWith(".tif")) {
								File cutedFile = new File(cutScenar, fileFile.getName());
								Rasters.cut(fileFile, empriseFile, cutedFile);
							}
						}
					}
				}
			}
		}
	}

}
