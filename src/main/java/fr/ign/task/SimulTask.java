package fr.ign.task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;

import javax.imageio.spi.IIORegistry;
import javax.media.jai.JAI;
import javax.media.jai.OperationRegistry;

import org.thema.mupcity.AHP;
import org.thema.mupcity.Project;
import org.thema.mupcity.scenario.ScenarioAuto;

import com.sun.media.jai.imageioimpl.ImageReadWriteSpi;

import it.geosolutions.imageio.stream.input.spi.URLImageInputStreamSpi;

public class SimulTask {
//    static {
//        IIORegistry.getDefaultInstance().registerServiceProvider(new URLImageInputStreamSpi());
//    }
    protected static void initJAI() {
        
        // See [URL]http://docs.oracle.com/cd/E17802_01/products/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/OperationRegistry.html[/URL]
        OperationRegistry registry = JAI.getDefaultInstance().getOperationRegistry();
        if( registry == null) {
            System.out.println("Error with JAI initialization (needed for GeoTools).");
        } else {
            try {
                new ImageReadWriteSpi().updateRegistry(registry);
            } catch(IllegalArgumentException e) {
                // Probably indicates it was already registered.
            }
        }
    }
	public static boolean saveWholeProj = false;
	public static String nameTot;

	public static void main(String[] args) throws Exception {
		File projFile = new File("/home/mcolomb/workspace/mupcity-openMole/result/emprise/emprise-Data1.0");

		String name = "emprise-Data1.0";

		// for yager
		double ahp0 = 0.111;
		double ahp1 = 0.111;
		double ahp2 = 0.111;
		double ahp3 = 0.111;
		double ahp4 = 0.111;
		double ahp5 = 0.111;
		double ahp6 = 0.111;
		double ahp7 = 0.111;
		double ahp8 = 0.111;

		// for moy
		// double ahp0 = 1;
		// double ahp1 = 1;
		// double ahp2 = 1;
		// double ahp3 = 1;
		// double ahp4 = 1;
		// double ahp5 = 1;
		// double ahp6 = 1;
		// double ahp7 = 1;
		// double ahp8 = 1;

		boolean strict = false;
		boolean mean = true;
		int nMax = 5;
		long seed = 42;
		// for (long seed = 42; seed <= 1042; seed = seed + 1) {
		run(projFile, name, nMax, strict, ahp0, ahp1, ahp2, ahp3, ahp4, ahp5, ahp6, ahp7, ahp8, mean, seed);
		// }
	}

	public static File run(File decompFile, String name, int nMax, boolean strict, double ahp0, double ahp1, double ahp2, double ahp3, double ahp4, double ahp5, double ahp6,
			double ahp7, double ahp8, boolean mean, long seed) throws Exception {
	    initJAI();
		return run(decompFile, name, nMax, strict, prepareAHP(ahp0, ahp1, ahp2, ahp3, ahp4, ahp5, ahp6, ahp7, ahp8), mean, seed);
	}

	public static File run(File projectFile, String name, int nMax, boolean strict, AHP ahp, boolean mean, long seed) throws Exception {

		setName(name);
		Project project = Project.load(new File(projectFile, name + ".xml"));
		String nBa = "Ba";
		if (strict) {
			nBa = "St";
		}
		String nYag = "Yag";
		if (mean) {
			nYag = "Moy";
		}
		String scenarName = "N" + String.valueOf(nMax) + "_" + nBa + "_" + nYag + "_ahpx" + "_seed_" + String.valueOf(seed);
		File projOut = new File(projectFile, scenarName);
		projOut.mkdir();

		boolean useNU = true;

		System.out.println(project.getInfoLayer());
		if (!project.hasNoBuild()) {
			useNU = false;
		}

		NavigableSet<Double> res = project.getMSGrid().getResolutions();
		ScenarioAuto scenario = ScenarioAuto.createMultiScaleScenario(scenarName, res.first(), res.last(), nMax, strict, ahp, useNU, mean, 3, seed, false, false);
		project.performScenarioAuto(scenario);
		scenario.extractEvalAnal(projOut, project);

		setName(ProjectCreationDecompTask.getName()+"_"+scenarName);
		
		// save the project
		if (saveWholeProj) {
			scenario.save(projOut, project);
			scenario.extractEvalAnal(projOut, project);
			project.getMSGrid().saveRaster(scenarName + "-eval", projOut);
		}
		return projOut;

	}

	private static void setName(String newName){
		nameTot = newName; 
	}
	
	public static String getName(){
		return nameTot;
	}
	
	private static AHP prepareAHP(double ahp0, double ahp1, double ahp2, double ahp3, double ahp4, double ahp5, double ahp6, double ahp7, double ahp8) {
		List<String> items = new ArrayList<>();
		items.add("morpho");
		items.add("road");
		items.add("fac1");
		items.add("fac2");
		items.add("fac3");
		items.add("pt");
		items.add("lei1");
		items.add("lei2");
		items.add("lei3");
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
