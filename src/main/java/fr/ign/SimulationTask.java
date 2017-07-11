package fr.ign;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NavigableSet;

import org.thema.mupcity.AHP;
import org.thema.mupcity.Project;
import org.thema.mupcity.scenario.ScenarioAuto;

public class SimulationTask {

	public static void main(String[] args) throws Exception {
		String name = "project";
		File folderIn = new File("./data");
		File projFile = new File(folderIn, name);
		boolean strict = true;
		double ahp0 = 0.111;
		double ahp1 = 0.111;
		double ahp2 = 0.111;
		double ahp3 = 0.111;
		double ahp4 = 0.111;
		double ahp5 = 0.111;
		double ahp6 = 0.111;
		double ahp7 = 0.111;
		double ahp8 = 0.111;
		boolean mean = true;
		long seed = 42L;
		for (int nMax = 4; nMax <= 7; nMax++) {
			run(projFile, name, nMax, strict, ahp0, ahp1, ahp2, ahp3, ahp4, ahp5, ahp6, ahp7, ahp8, mean, seed);
		}
	}

	public static File run(File decompFile, String name, int nMax, boolean strict, double ahp0, double ahp1, double ahp2, double ahp3, double ahp4,
	    double ahp5, double ahp6, double ahp7, double ahp8, boolean mean, long seed) throws Exception {
		return run(decompFile, name, nMax, strict, prepareAHP(ahp0, ahp1, ahp2, ahp3, ahp4, ahp5, ahp6, ahp7, ahp8), mean, seed);
	}

	public static File run(File decompFile, String name, int nMax, boolean strict, AHP ahp, boolean mean, long seed) throws Exception {
		Project project = Project.load(new File(decompFile, name + ".xml"));
		String nBa = (strict) ? "St" : "Ba";
		String nYag = (mean) ? "Moy" : "Yag";
		String scName = "N" + String.valueOf(nMax) + "_" + strict + "_" + mean + "_" + nBa + "_" + nYag + "_ahpx" + "_seed_" + String.valueOf(seed);
		File projOut = new File(decompFile, scName);
		projOut.mkdirs();
		NavigableSet<Double> res = project.getMSGrid().getResolutions();
		ScenarioAuto s = ScenarioAuto.createMultiScaleScenario(scName, res.first(), res.last(), nMax, strict, ahp, true, mean, 3, seed, false, false);
		project.performScenarioAuto(s);
		// save the project
		s.save(projOut, project);
		s.extractEvalAnal(projOut, project);
		project.getMSGrid().saveRaster(scName + "-eval", projOut);
		return projOut;
	}

	private static AHP prepareAHP(double ahp0, double ahp1, double ahp2, double ahp3, double ahp4, double ahp5, double ahp6, double ahp7, double ahp8) {
		List<String> items = Arrays.asList("morpho", "road", "fac1", "fac2", "fac3", "pt", "lei1", "lei2", "lei3");
		AHP ahpE_Moy = new AHP(items);
		ahpE_Moy.setCoef(items.get(8), ahp8);
		ahpE_Moy.setCoef(items.get(7), ahp7);
		ahpE_Moy.setCoef(items.get(6), ahp6);
		ahpE_Moy.setCoef(items.get(5), ahp5);
		ahpE_Moy.setCoef(items.get(4), ahp4);
		ahpE_Moy.setCoef(items.get(3), ahp3);
		ahpE_Moy.setCoef(items.get(2), ahp2);
		ahpE_Moy.setCoef(items.get(1), ahp1);
		ahpE_Moy.setCoef(items.get(0), ahp0);
		return ahpE_Moy;
	}
}
