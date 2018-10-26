package fr.ign.task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.MutablePair;
import org.ejml.ops.ReadCsv;
import org.geotools.geometry.DirectPosition2D;
import org.math.plot.utils.Array;

import au.com.bytecode.opencsv.CSVReader;
import fr.ign.analyse.FractalDimention;
import fr.ign.analyse.RasterAnalyse;
import fr.ign.analyse.RasterMerge;
import fr.ign.analyse.RasterMergeResult;
import fr.ign.analyse.obj.Analyse;
import fr.ign.analyse.obj.ProjetAnalyse;
import fr.ign.analyse.obj.ScenarAnalyse;
import fr.ign.cogit.GTFunctions.Csv;
import fr.ign.exp.minCellSize;

public class AnalyseTask {

	// public static String echelle;

	public static void main(String[] args) throws Exception {

		makezoningDiagram(new File("/media/mcolomb/Data_2/resultFinal/sens/cellSize/cuted/result--cellSize"));
		// File file = new File("/media/mcolomb/Data_2/resultFinal/sens/cellSize/cuted");
		// File fileDonnee = new File("/media/mcolomb/Data_2/dataOpenMole/all");
		// RasterAnalyse.rootFile = file;
		// RasterAnalyse.echelle = "20";
		//
		// runSizeCellExplo(file, fileDonnee, null, "cellSize", false);

		// File file = new File("/media/mcolomb/Data_2/resultFinal/sens/cellSize");
		// File fileDonnee = new File("/media/mcolomb/Data_2/dataOpenMole/stabilite/dataManu");
		// runSizeCellExplo(file, fileDonnee, "cellSize", false);

		// File file = new File("/media/mcolomb/Data_2/resultFinal/sens/GridMouv");
		// File fileDonnee = new File("/media/mcolomb/Data_2/dataOpenMole/stabilite/dataAutom");
		// RasterAnalyse.rootFile = file;
		// RasterAnalyse.echelle = "20";
		// runGridExplo(file,fileDonnee,"20", "GridMouv", false);

		// List<File> toCompare = new ArrayList<File>();
		// toCompare.add(new
		// File("/media/mcolomb/Data_2/resultFinal/testAHP/2emevague/StabiliteTestAHP-Autom-CM20.0-S0.0-GP_915948.0_6677337.0/N5_Ba_MoyahpS_Moy_seed_42/N5_Ba_MoyahpS_Moy_seed_42-evalAnal-20.0.tif"));
		// toCompare.add(new
		// File("/media/mcolomb/Data_2/resultFinal/testAHP/2emevague/StabiliteTestAHP-Autom-CM20.0-S0.0-GP_915948.0_6677337.0/N5_Ba_MoyahpE_Moy_seed_42/N5_Ba_MoyahpE_Moy_seed_42-evalAnal-20.0.tif"));
		// toCompare.add(new
		// File("/media/mcolomb/Data_2/resultFinal/testAHP/2emevague/StabiliteTestAHP-Autom-CM20.0-S0.0-GP_915948.0_6677337.0/N5_Ba_MoyahpT_Moy_seed_42/N5_Ba_MoyahpT_Moy_seed_42-evalAnal-20.0.tif"));
		//
		// compTwoSimus(toCompare,RasterAnalyse.statFile.getParentFile(),"20");

		// File AHPFile = new
		// File("/media/mcolomb/Data_2/resultFinal/testAHP/troisiemeVague/StabiliteTestAHP-Autom-CM20.0-S0.0-GP_915948.0_6677337.0");
		// List<File> dejafa = new ArrayList<File>();
		//
		// for (File f : AHPFile.listFiles()) {
		// if (!f.isDirectory()) {
		// File dir = new File(f.getParentFile(), f.getName().split("-")[0]);
		// if (!dejafa.contains(dir)) {
		// dir.mkdir();
		// dejafa.add(dir);
		// }
		//
		// OutputStream o = new FileOutputStream(new File(dir,f.getName()));
		// Files.copy(f.toPath(), o);
		//
		// }
		// }

		// //cellules par rapport aux zones constructibles
		// List<File> lF = new ArrayList<File>();
		// lF.add(new File(
		// "/media/mcolomb/Data_2/resultFinal/stab/result--Stabilite/dataManu-CM20.0-S0.0-GP_915948.0_6677337.0--N4_St_Moy_ahpx/SortieExemple/"));
		// lF.add(new File(
		// "/media/mcolomb/Data_2/resultFinal/stab/result--Stabilite/dataManu-CM20.0-S0.0-GP_915948.0_6677337.0--N6_St_Moy_ahpx/SortieExemple/"));
		// lF.add(new File(
		// "/media/mcolomb/Data_2/resultFinal/stab/result--Stabilite/dataManu-CM20.0-S0.0-GP_915948.0_6677337.0--N5_Ba_Moy_ahpx/SortieExemple/"));
		// lF.add(new File(
		// "/media/mcolomb/Data_2/resultFinal/stab/result--Stabilite/dataManu-CM20.0-S0.0-GP_915948.0_6677337.0--N7_Ba_Yag_ahpx/SortieExemple/"));
		//
		// for (File f : lF) {
		//
		// for (File ff : f.listFiles()) {
		// if (ff.getName().contains("evalAnal-20.0.tif")) {
		// RasterAnalyse.echelle = "20";
		// RasterAnalyse.statFile = new File("/home/mcolomb/tmp/");
		//
		// RasterMergeResult salut = RasterAnalyse.mergeRasters(ff);
		//
		// String[] champ = { "Zones Constructibles", "LIBELLE" };
		//
		// RasterAnalyse.createStatsDiscrete(
		// f.getName().replace("_ahpx_seed_6111434047454933700-evalAnal-20.0.tif",
		// "").replace("tability-dataManu-CM20.0-S0.0-GP_915948.0_6677337.0--", ""),
		// salut,
		// new File("/media/mcolomb/Data_2/donnee/docUrbaLocalTotssUx.shp"), champ);
		// }
		// }
		// }

		// //compdata
		// File totFile = new File("/media/mcolomb/Data_2/resultFinal/compData");
		// File[] totFiles = new File[totFile.listFiles().length];
		// String[] names = { "CompDonneeLight" };
		// int i = 0;
		// for (File f : totFile.listFiles()) {
		// if (f.getName().startsWith(names[0])) {
		// totFiles[i] = f;
		// i++;
		// }
		// }
		// File[] totInFile = { new
		// File("/home/mcolomb/workspace/mupcity-openMole/data/") };
		// // File file = new
		// File("/home/mcolomb/workspace/mupcity-openMole/result/compDonneeTest/ScenarVrac");
		// runCompData(totFiles, totInFile, new File("/home/mcolomb/tmp"), names, true);

	}

	// public static File runGridSens(File[] file, File[] fileDonnee, File mainFile,
	// String[] name, boolean machineReadable) throws Exception {
	//
	// return runGridSens(copyToScenVrac(fileDonnee, mainFile), fileDonnee[0],
	// name[0], machineReadable);
	// }

	/**
	 * Analyse automatique des explorations faites avec le script MupCityGridExplo.oms
	 * 
	 * @param file
	 *            : le dossier ou sont contenus les résultats
	 * @param minCell
	 *            : taille minimale des cellules
	 * @param discreteFile
	 *            : le shapefile contenant les entitées permettant une discrétisation (dans la pluspart des cas, les communes)
	 * @param batiFile
	 *            : le shapefile contenant les batiments
	 * @param name
	 *            : le nom de la simulation (doit être le même que pour le projet)
	 * @return le dossier où sont contenus les fichiers produits
	 * @throws Exception
	 */
	// public static File runGridSens(File file, File fileDonnee, String name,
	// boolean machineReadable) throws Exception {
	//
	// RasterAnalyse.rootFile = file;
	// RasterAnalyse.cutBorder = true;
	//
	// File discreteFile = getDiscrete(fileDonnee);
	//
	// File resultFile = new File(file, "result-" + name);
	// if (machineReadable) {
	// resultFile = new File(file.getParentFile(), "result--" + name);
	// }
	//
	// File rastFile = new File(resultFile, "raster");
	//
	// Analyse anal = new Analyse();
	// if (machineReadable) {
	// anal = new Analyse(file, name, machineReadable);
	// } else {
	// anal = new Analyse(file, name);
	// }
	//
	// // compare the effect of the minimal sizes of the cells
	// List<Set<ScenarAnalyse>> listCellMin =
	// anal.getScenars(anal.getProjetByCellmin());
	//
	// for (Set<ScenarAnalyse> list : listCellMin) {
	// File statFile = new File(resultFile, "stat-" +
	// list.get(0).getProjFile().getName() + "-" + list.get(0).getScenarName());
	// statFile.mkdirs();
	// RasterAnalyse.statFile = statFile;
	//
	// // create analysis of different parameter setting regarding to different size
	// of cells
	// RasterAnalyse.compareDiffSizedCell(list, anal, name, discreteFile);
	// }
	//
	// // compare les réplication entre les seuils
	//
	// List<List<ScenarAnalyse>> listsSeuil = anal.getProjetBySeuil();
	//
	// for (List<ScenarAnalyse> listSeuil : listsSeuil) {
	// File statFile = new File(resultFile, "stat-" + listSeuil.get(0).getSeuil() +
	// "-" + listSeuil.get(0).getGrid() + "-" + listSeuil.get(0).getScenarName());
	// statFile.mkdirs();
	// RasterAnalyse.statFile = statFile;
	//
	// RasterMergeResult resultMergedSeuil = RasterAnalyse.mergeRasters(listSeuil,
	// anal, listSeuil.get(0));
	// RasterAnalyse.createStatsDescriptive(name + ("SeuilComparison"),
	// resultMergedSeuil);
	// // don't know why this one crashes coz of a lock
	// RasterAnalyse.createStatsDiscrete(name + ("SeuilComparisonDiscrete"),
	// resultMergedSeuil, discreteFile);
	// rastFile.mkdirs();
	// RasterMerge.merge(listSeuil, anal,
	// new File(rastFile,
	// "rasterMerged-SeuilComp" + listSeuil.get(0).getSizeCell() + "-" +
	// listSeuil.get(0).getSeuil() + "-" + listSeuil.get(0).getScenarName() +
	// ".tif"),
	// RasterAnalyse.cutBorder);
	// }
	//
	// // compare les réplication entre les grilles
	//
	// System.out.println("------------(((Grid)))------------");
	// List<List<ScenarAnalyse>> listsGrid = anal.getProjetByGrid();
	//
	// for (List<ScenarAnalyse> listGrid : listsGrid) {
	// File statFile = new File(resultFile, "stat-" + listGrid.get(0).getSizeCell()
	// + "-" + listGrid.get(0).getSeuil() + "-" + listGrid.get(0).getScenarName());
	// statFile.mkdirs();
	// RasterAnalyse.statFile = statFile;
	//
	// RasterMergeResult resultMergedSeuil = RasterAnalyse.mergeRasters(listGrid,
	// anal, listGrid.get(0));
	// RasterAnalyse.createStatsDescriptive(name + ("GridComparison"),
	// resultMergedSeuil);
	// RasterAnalyse.createStatsDiscrete(name + ("GridComparisonDiscrete"),
	// resultMergedSeuil, discreteFile);
	// rastFile.mkdirs();
	// RasterMerge.merge(listGrid, anal,
	// new File(rastFile,
	// "rasterMerged-SeuilComp" + listGrid.get(0).getSizeCell() + "-" +
	// listGrid.get(0).getSeuil() + "-" + listGrid.get(0).getScenarName() + ".tif"),
	// RasterAnalyse.cutBorder);
	// }
	//
	// // test the effect of grid - compare les réplication entre les seuils et les
	// différentes grilles
	//
	// return resultFile;
	// }

	/**
	 * overlaoding to use aggregation transition from openMole. It copies all the files into a mainFile folder calles 'ScenarVrac'
	 * 
	 * @param file
	 * @param fileDonnee
	 * @param name
	 * @param machineReadable
	 * @return
	 * @throws Exception
	 */
	public static File runGridExplo(File[] file, File[] fileDonnee, File mainFile, String[] name, boolean machineReadable) throws Exception {
		return runStab(copyToScenVrac(file, mainFile), fileDonnee[0], name[0], machineReadable);
	}

	public static String getNameProjetByScenarSet(Set<ScenarAnalyse> setScenar) {
		String projName = "";
		for (ProjetAnalyse sA : setScenar) {
			projName = sA.getNiceName();
			System.out.println("projet : " + projName);
			break;
		}
		return projName;
	}

	public static File runGridMouvData(File file, File fileDonnee, String echelle, String name, boolean machineReadable) throws Exception {

		// folder settings
		File discreteFile = getDiscrete(fileDonnee);
		File resultFile = new File(file, "result--" + name);
		if (machineReadable) {
			resultFile = new File(file.getParentFile(), "result--" + name);
		}

		resultFile.mkdir();
		RasterAnalyse.rootFile = file;
		RasterAnalyse.echelle = echelle;

		// toutes les listes des projets à tester

		RasterAnalyse.echelle = echelle;
		// pour l'analyse des différents seuils
		List<List<File>> toStudyListFile = new ArrayList<List<File>>();
		for (int n = 4; n <= 7; n++) {
			List<File> scenarList = new ArrayList<File>();
			for (File projectFile : file.listFiles()) {
				if (projectFile.getName().startsWith(name)) {
					for (File scenarFile : projectFile.listFiles()) {
						if (scenarFile.isDirectory() || scenarFile.getName().startsWith("N" + n)) {
							for (File ff : scenarFile.listFiles()) {
								if (ff.getName().startsWith("N" + n) && ff.getName().endsWith("evalAnal-20.0.tif")) {
									scenarList.add(ff);
								}
							}

						}
					}
				}
			}
			toStudyListFile.add(scenarList);
		}

		for (List<File> fileToStudy : toStudyListFile) {

			String exScenarName = fileToStudy.get(0).getParentFile().getName();
			System.out.println("name scenar : " + exScenarName);

			File eachResultFile = new File(resultFile, exScenarName);

			File statFile = new File(eachResultFile, "stat");
			RasterAnalyse.statFile = statFile;
			statFile.mkdirs();
			File rastFile = new File(eachResultFile, "raster");
			rastFile.mkdirs();

			// merge the different input rasters

			RasterMergeResult mergedResult = RasterAnalyse.mergeRasters(fileToStudy);

			// get the average evaluation of cells in a .csv
			if (!mergedResult.getCellEval().isEmpty()) {
				RasterAnalyse.createStatEvals(mergedResult.getCellEval());
			}

			// RasterAnalyse.createStatsDescriptive("analyse-dataMouv---" + exScenarName, mergedResult, 2);
			// discrete statistics on the cities and on the allowed to urbanise zones
			// String[] champ = { "Zoning", "TYPEZONE" };
			// RasterAnalyse.createStatsStabDiscrete(exScenarName, mergedResult, new File(fileDonnee, "zonage.shp"), champ);
			RasterAnalyse.createCellsPerCities(exScenarName, fileToStudy, discreteFile);
			// create a merged raster
			// RasterMerge.merge(fileToStudy, new File(rastFile, "rastMerge-"+exScenarName+".tif"), 20);
		}

		return resultFile;
	}

	public static File runGridExplo(File file, File fileDonnee, String echelle, String name, boolean machineReadable) throws Exception {

		// folder settings
		File discreteFile = getDiscrete(fileDonnee);
		File resultFile = new File(file, "result--" + name);
		if (machineReadable) {
			resultFile = new File(file.getParentFile(), "result--" + name);
		}

		resultFile.mkdir();
		RasterAnalyse.rootFile = file;
		RasterAnalyse.cutBorder = true;
		RasterAnalyse.saveEvalTab = true;
		RasterAnalyse.echelle = echelle;

		// toutes les listes des projets à tester
		Analyse anal = new Analyse();
		if (machineReadable) {
			anal = new Analyse(file, name, machineReadable);
		} else {
			anal = new Analyse(file, name);
		}

		RasterAnalyse.echelle = echelle;
		// pour l'analyse des différents seuils

		for (Set<ScenarAnalyse> scenarPerGrid : anal.getProjetByGrid()) {

			String exScenarName = scenarPerGrid.iterator().next().getNiceName().split("--")[1];
			if (exScenarName.equals("N5_Ba_Moy_ahpE_seed_42") || exScenarName.equals("N6_St_Moy_ahpE_seed_42")) {
				File eachResultFile = new File(resultFile, exScenarName);

				File statFile = new File(eachResultFile, "stat");
				RasterAnalyse.statFile = statFile;
				File rastFile = new File(eachResultFile, "raster");
				rastFile.mkdirs();
				File middleGridRaster = new File("");
				// get the set of files to test
				List<File> fileToTest = new ArrayList<File>();
				for (ScenarAnalyse sC : scenarPerGrid) {
					fileToTest.add(anal.getSimuFile(sC, echelle, "evalAnal"));
					// put the middle grid info
					if (sC.getGrid().equals(anal.getMiddleGrid())) {
						middleGridRaster = anal.getSimuFile(sC, echelle, "evalAnal");
						RasterAnalyse.middleGridRaster = middleGridRaster;
						RasterMerge.middleGridRaster = middleGridRaster;
					}
				}
				// merge the different input rasters

				RasterMergeResult mergedResult = RasterAnalyse.mergeRasters(fileToTest);

				// get the average evaluation of cells in a .csv
				if (!mergedResult.getCellEval().isEmpty()) {
					RasterAnalyse.createStatEvals(mergedResult.getCellEval());
				}

				RasterAnalyse.createStatsDescriptive("analyse-grid---" + exScenarName, mergedResult, 2, false);
				// discrete statistics on the cities and on the allowed to urbanise zones
				String[] champ = { "Cities", "NOM_COM" };
				RasterAnalyse.createStatsStabDiscrete(exScenarName, mergedResult, discreteFile, champ);
				RasterAnalyse.createCellsPerCities(exScenarName, fileToTest, discreteFile);
				// create a merged raster
				RasterMerge.writeGeotiff(mergedResult.getCellRepet(), new File(rastFile, exScenarName + "-" + name + "-rasterMerged-" + echelle + ".tif"),
						Integer.parseInt(echelle), middleGridRaster);
			}
		}
		return resultFile;
	}

	public static File runEffetSeuil(File file, File fileDonnee, String echelle, String name, boolean machineReadable) throws Exception {

		// folder settings

		File resultFile = new File(file, "result--" + name);
		if (machineReadable) {
			resultFile = new File(file.getParentFile(), "result--" + name);
		}
		File discreteFile = getDiscrete(fileDonnee);
		resultFile.mkdir();
		RasterAnalyse.rootFile = file;
		RasterAnalyse.echelle = echelle;

		// toutes les listes des projets à tester
		Analyse anal = new Analyse();
		if (machineReadable) {
			anal = new Analyse(file, name, machineReadable);
		} else {
			anal = new Analyse(file, name);
		}

		RasterAnalyse.echelle = echelle;
		// pour l'analyse des différents seuils

		for (Set<ScenarAnalyse> scenarPerGrid : anal.getProjetBySeuil()) {

			String exScenarName = scenarPerGrid.iterator().next().getNiceName().split("--")[1];
			File eachResultFile = new File(resultFile, exScenarName);

			File statFile = new File(eachResultFile, "stat");
			RasterAnalyse.statFile = statFile;
			File rastFile = new File(eachResultFile, "raster");
			rastFile.mkdirs();
			// get the set of files to test
			List<File> fileToTest = new ArrayList<File>();
			for (ScenarAnalyse sC : scenarPerGrid) {
				fileToTest.add(anal.getSimuFile(sC, echelle, "evalAnal"));
			}
			// merge the different input rasters

			RasterMergeResult mergedResult = RasterAnalyse.mergeRasters(fileToTest);

			// get the average evaluation of cells in a .csv
			// if (!mergedResult.getCellEval().isEmpty()) {
			// RasterAnalyse.createStatEvals(mergedResult.getCellEval());
			// }
			//
			RasterAnalyse.createStatsDescriptive("analyse-grid---" + exScenarName, mergedResult);

			// single simulation to analyse
			ScenarAnalyse singleSim = scenarPerGrid.iterator().next();
			// discrete statistics on the cities and on the allowed to urbanise zones
			RasterAnalyse.createStatDiscreteSingleSimu(fileToTest.get(0).getName() + "-" + singleSim.getSeuil(), anal.getSimuFile(singleSim), discreteFile);
			// RasterAnalyse.createCellPerCities(exScenarName, fileToTest, discreteFile);
			// create a merged raster
			RasterMerge.writeGeotiff(mergedResult.getCellRepet(), new File(rastFile, exScenarName + "-" + name + "-rasterMerged-" + echelle + ".tif"), Integer.parseInt(echelle),
					fileToTest.get(0));
		}

		return resultFile;
	}

	public static File runSizeCellExplo(File file, File fileDonnee, File empriseFile, String name, boolean machineReadable) throws Exception {
		// folder settings
		File discreteFile = getDiscrete(fileDonnee);
		File zoningFile = getZoningFile(fileDonnee);
		File resultFile = new File(file, "result--" + name);
		if (machineReadable) {
			resultFile = new File(file.getParentFile(), "result--" + name);
		}

		// cut the scearios to the exact same cells
		// file = minCellSize.cutOut(file, name, empriseFile);

		resultFile.mkdir();
		RasterAnalyse.rootFile = file;

		// toutes les listes des projets à tester
		Analyse anal = new Analyse();
		if (machineReadable) {
			anal = new Analyse(file, name, machineReadable);
		} else {
			anal = new Analyse(file, name);
		}

		// for (List<ScenarAnalyse> arL : anal.getScenarDiffSeed()) {
		// for (String echelle : anal.getEchelleRange(1, arL.get(0))) {
		// System.out.println("echelle : " + echelle);
		// System.out.println(arL.get(0).getNiceName());
		// String nameTest = new String(arL.get(0).getNiceNameWthSeed());
		//
		// File eachResultFile = new File(resultFile, nameTest);
		// eachResultFile.mkdirs();
		//
		// File statFile = new File(eachResultFile, "stat");
		// RasterAnalyse.statFile = statFile;
		// File rastFile = new File(eachResultFile, "raster");
		// rastFile.mkdir();
		//
		// RasterAnalyse.echelle = echelle;
		// List<File> fileToTest = new ArrayList<File>();
		// // get the set of files to test
		// for (ScenarAnalyse sC : arL) {
		// fileToTest.add(anal.getSimuFile(sC, echelle, "evalAnal"));
		// }
		//
		// // merge the different input rasters
		// RasterMergeResult mergedResult = RasterAnalyse.mergeRasters(fileToTest);
		//
		// // statistics for the simple task with those objects
		// RasterAnalyse.createStatsDescriptive(nameTest, mergedResult, true);
		//
		// RasterAnalyse.createStatsStabDiscrete(arL.get(0).getNiceNameWthSeed() + "-" + arL.get(0).getSizeCell(), mergedResult, discreteFile);
		// String[] differentObjects3 = { "zoning", "TYPEZONE" };
		// RasterAnalyse.createStatsStabDiscrete(arL.get(0).getNiceNameWthSeed() + "-" + arL.get(0).getSizeCell(), mergedResult, zoningFile, differentObjects3);
		//
		// }
		// }
		// for (List<ScenarAnalyse> arL : anal.getScenarOneSeed()) {
		// for (String echelle : anal.getEchelleRange(1, arL.get(0))) {
		// System.out.println("echelle : " + echelle);
		// System.out.println(arL.get(0).getNiceName());
		// String nameTest = new String(arL.get(0).getNiceNameWthSeed());
		//
		// File eachResultFile = new File(resultFile, nameTest);
		// eachResultFile.mkdirs();
		//
		// File statFile = new File(eachResultFile, "stat");
		// RasterAnalyse.statFile = statFile;
		//
		// RasterAnalyse.echelle = echelle;
		//
		// // discrete stat (for the number and not the area)
		// RasterAnalyse.createStatDiscreteSingleSimu(arL.get(0).getNiceNameWthSeed() + "-" + arL.get(0).getSizeCell(), anal.getSimuFile(arL.get(0), echelle, "evalAnal"),
		// discreteFile );
		// String[] differentObjects3 = { "zoning", "TYPEZONE" };
		//
		// RasterAnalyse.createStatDiscreteSingleSimu(arL.get(0).getNiceNameWthSeed() + "-" + arL.get(0).getSizeCell(), anal.getSimuFile(arL.get(0), echelle, "evalAnal"),
		// new File("/media/mcolomb/Data_2/donnee/ZC-simple.shp"), differentObjects3);
		//
		//
		// RasterAnalyse.createCellsPerCities(nameScenar, fileRepli, discreteFile);
		//
		// }
		// }

		for (List<File> salut : anal.getScenarOneSeed("42")) {
			for (File f : salut) {
				System.out.println(f);
			}
			System.out.println();
		}

		// makeAreaDiagram(resultFile);
		return resultFile;

	}

	public static void makeAreaDiagram(File base) throws IOException {
		String[] fLine = { "sizeCell", "surface" };
		for (int n = 4; n <= 7; n++) {
			Hashtable<String, double[]> result = new Hashtable<String, double[]>();
			for (File f : base.listFiles()) {
				if (f.isDirectory() && f.getName().startsWith("Manu") && f.getName().split("--")[1].startsWith("N" + n)) {
					System.out.println(f.getName());
					String sizeCell = f.getName().split("CM")[1].split("-")[0];
					CSVReader csv = new CSVReader(new FileReader((new File(f, "stat/descriptive_statistics.csv"))));
					String[] firstLine = csv.readNext();
					int surfIndice = 0;
					for (int i = 0; i < firstLine.length; i++) {
						if (firstLine[i].startsWith("surface")) {
							surfIndice = i;
						}
					}
					double[] put = { Double.valueOf(csv.readNext()[surfIndice]) / 1000000 };
					result.put(sizeCell, put);
					csv.close();
				}
			}
			Csv.generateCsvFile(result, new File(base, "surface-scenar-N" + n), "scenar-N" + n, fLine);
		}
	}
//TODO make that work
	public static void makezoningDiagram(File base) throws IOException {
		String[] fLine = { "sizeCell", "surfaceAU", "surfaceA", "surfaceNC", "surfaceZC", "surfaceU", "surfaceN" };
		for (int n = 4; n <= 7; n++) {
			Hashtable<String, double[]> result = new Hashtable<String, double[]>();
			//pour les 10 simus
			for (File f : base.listFiles()) {
				if (f.isDirectory() && f.getName().startsWith("Manu") && f.getName().split("--")[1].startsWith("N" + n)) {
					
					System.out.println(f.getName());
					String sizeCell = f.getName().split("CM")[1].split("-")[0];
					CSVReader csv = new CSVReader(new FileReader((new File(f, "stat/cellByzoning.csv"))));
					String[] firstLine = csv.readNext();
					int surfIndice = 0;
					for (int i = 0; i < firstLine.length; i++) {
						if (firstLine[i].startsWith("zoning")) {
							surfIndice = i;
						}
					}
					csv.close();
					
					
					double[] put = new double[6];
					for (int i = 0; i < 6; i++) {
						System.out.println("i   :    "+i);
						CSVReader csv2 = new CSVReader(new FileReader((new File(f, "stat/cellByzoning.csv"))));
						csv2.readNext();
						int count = 0;
						for (String[] line : csv2.readAll()) {
							if (count == i ) {
							put[i] = Double.valueOf(line[surfIndice]) * Math.pow(Double.valueOf(sizeCell), 2) / 1000000;
							System.out.println("line[surfIndice]   : "+Double.valueOf(line[surfIndice]) * Math.pow(Double.valueOf(sizeCell), 2) / 1000000);
						
							}
							count++;
						}
						csv2.close();
					}
					result.put(sizeCell, put);
					
				}
			}
			Csv.generateCsvFile(result, new File(base, "surfaceZoning-scenar-N" + n), "scenar-N" + n, fLine);
		}
	}

	private static File getZoningFile(File fileDonnee) {
		File zoningFile = new File("");
		for (File filesDonnee : fileDonnee.listFiles()) {
			try {
				for (File fileShp : filesDonnee.listFiles()) {
					if (fileShp.getName().equals("zonage.shp")) {
						zoningFile = fileShp;
						break;
					}
				}
			} catch (NullPointerException e) { // Si les données sont toutes dans un unique répertoire
				if (filesDonnee.getName().equals("zonage.shp")) {
					zoningFile = filesDonnee;
					break;
				}
			}
		}
		return zoningFile;
	}

	public static void compTwoSimus(List<File> fileToTest, File fileOut, String echelle) throws Exception {

		String nameTest = fileToTest.get(0).getName();
		File mainFile = fileOut;

		for (File f : fileToTest) {
			if (!f.equals(fileToTest.get(0))) {
				nameTest = nameTest.concat("-comparedTo-" + f.getName());
			}
		}

		RasterAnalyse.echelle = echelle;
		RasterAnalyse.statFile = new File(fileOut, "stat");
		RasterMergeResult mergedResult = RasterAnalyse.mergeRasters(fileToTest);
		RasterAnalyse.createStatsDescriptive(nameTest, mergedResult);
		RasterMerge.merge(fileToTest, new File(mainFile, nameTest + "-rasterMerged-" + echelle + ".tif"), Integer.parseInt(echelle));

		System.out.println("Ça se trouve dans " + mainFile);
	}

	public static void compProjects(List<File> projectToTest, String name, File fileOut, String echelle) throws Exception {
		Hashtable<String, List<File>> scenarCollec = new Hashtable<String, List<File>>();
		for (File projet : projectToTest) {
			if (projet.getName().startsWith(name)) {
				for (File scenar : projet.listFiles()) {
					if (scenar.getName().startsWith("N") && scenar.isDirectory()) {
						for (File file : scenar.listFiles()) {
							if (file.getName().endsWith("evalAnal-" + echelle + ".0.tif")) {

								if (scenarCollec.containsKey(scenar.getName())) {
									List<File> tempList = scenarCollec.remove(scenar.getName());
									tempList.add(file);
									scenarCollec.put(scenar.getName(), tempList);
								} else {
									List<File> tmp = new ArrayList<File>();
									tmp.add(file);
									scenarCollec.put(scenar.getName(), tmp);
								}
							}
						}

					}
				}
			}
		}
		for (String scenar : scenarCollec.keySet()) {
			compTwoSimus(scenarCollec.get(scenar), fileOut, echelle);
		}
	}

	/**
	 * overlaoding to use aggregation transition from openMole. It copies all the files into a mainFile folder calles 'ScenarVrac'
	 * 
	 * @param file
	 * @param fileDonnee
	 * @param name
	 * @param machineReadable
	 * @return
	 * @throws Exception
	 */
	public static File runStab(File[] file, File[] fileDonnee, File mainFile, String[] name, boolean machineReadable) throws Exception {
		return runStab(copyToScenVrac(file, mainFile), fileDonnee[0], name[0], machineReadable);
	}

	public static File runStab(File file, File fileDonnee, String name, boolean machineReadable) throws Exception {
		// System.out.println("Initialization");
		Initialize.init();

		// folder settings
		File discreteFile = getDiscrete(fileDonnee);
		File resultFile = new File(file, "result--" + name);
		if (machineReadable) {
			resultFile = new File(file.getParentFile(), "result--" + name);
		}
		resultFile.mkdir();
		RasterAnalyse.rootFile = file;
		RasterAnalyse.stabilite = true;

		// toutes les listes des projets à tester
		Analyse anal = new Analyse();
		if (machineReadable) {
			anal = new Analyse(file, name, machineReadable);
		} else {
			anal = new Analyse(file, name);
		}
		// sélectionne des listes de scénario ayant pour différence la seed
		for (List<ScenarAnalyse> arL : anal.getScenarDiffSeed()) {

			// des objets inter-échelles
			Hashtable<DirectPosition2D, Float> SvgCellEval20 = new Hashtable<DirectPosition2D, Float>();
			Hashtable<DirectPosition2D, Integer> SvgCellRepet20 = new Hashtable<DirectPosition2D, Integer>();
			// pour tous les fichiers de ces listes

			// pour les trois premières échelles
			for (String echelle : anal.getEchelleRange(3, arL.get(0))) {
				System.out.println("!! Pour les scénarios " + arL.get(0).getProjFile().getName() + "_" + arL.get(0).getnMax() + "_" + arL.get(0).isStrict() + "_"
						+ arL.get(0).isYag() + "_" + arL.get(0).getAhp());
				int ech = Integer.valueOf(echelle);

				// convert Scenar to File
				String nameTest = arL.get(0).getProjFile().getName() + "_" + arL.get(0).getnMax() + "_" + arL.get(0).isStrict() + "_" + arL.get(0).isYag() + "_"
						+ arL.get(0).getAhp();

				if (machineReadable) {
					nameTest = new String(arL.get(0).getNiceNameWthSeed());
				}

				//
				File eachResultFile = new File(resultFile, nameTest);
				eachResultFile.mkdirs();

				File statFile = new File(eachResultFile, "stat");
				RasterAnalyse.statFile = statFile;
				File rastFile = new File(eachResultFile, "raster");
				rastFile.mkdir();

				File exampleFolder = new File("");

				// copy of an example
				if (machineReadable) {
					exampleFolder = copyExample(eachResultFile, anal.getSimuFiles(arL.get(0)));
				}

				RasterAnalyse.echelle = echelle;
				List<File> fileToTest = new ArrayList<File>();
				// get the set of files to test
				for (ScenarAnalyse sC : arL) {
					fileToTest.add(anal.getSimuFile(sC, echelle, "evalAnal"));
				}

				// merge the different input rasters
				RasterMergeResult mergedResult = RasterAnalyse.mergeRasters(fileToTest);

				// statistics for the simple task with those objects
				RasterAnalyse.createStatsDescriptive(nameTest, mergedResult);
				RasterAnalyse.createStatsEvol(mergedResult.getHisto(), echelle);

				// get the average evaluation of cells in a .csv
				if (!mergedResult.getCellEval().isEmpty()) {
					RasterAnalyse.createStatEvals(mergedResult.getCellEval());
				}
				// discrete statistics
				RasterAnalyse.createStatsStabDiscrete(nameTest, mergedResult, discreteFile);

				// create a merged raster
				RasterMerge.merge(fileToTest, new File(rastFile, nameTest + "-rasterMerged-" + echelle + ".tif"), Integer.parseInt(echelle));

				Integer minSizeCell = Integer.valueOf(arL.get(0).getSizeCell());
				// cells contained in superior scaled cells
				// reference simulation
				File concernedFile = getOutputExample(exampleFolder, ech);
				// Count how much minimal sized cells are contained into parent cells

				System.out.println("Inclusions des cellules");
				if (ech == minSizeCell) {
					SvgCellEval20 = RasterAnalyse.mergeRasters(concernedFile).getCellEval();
					SvgCellRepet20 = RasterAnalyse.mergeRasters(concernedFile).getCellRepet();
				} else if (ech == minSizeCell * 3) {
					Hashtable<DirectPosition2D, Float> cellEval60 = (Hashtable<DirectPosition2D, Float>) RasterAnalyse.mergeRasters(concernedFile).getCellEval();
					Hashtable<DirectPosition2D, Integer> cellRepet60 = (Hashtable<DirectPosition2D, Integer>) RasterAnalyse.mergeRasters(concernedFile).getCellRepet();
					RasterAnalyse.compareInclusionSizeCell(SvgCellRepet20, SvgCellEval20, cellRepet60, cellEval60, nameTest, ech);
				} else if (ech == minSizeCell * 9) {
					Hashtable<DirectPosition2D, Float> cellEval180 = (Hashtable<DirectPosition2D, Float>) RasterAnalyse.mergeRasters(concernedFile).getCellEval();
					Hashtable<DirectPosition2D, Integer> cellRepet180 = (Hashtable<DirectPosition2D, Integer>) RasterAnalyse.mergeRasters(concernedFile).getCellRepet();
					RasterAnalyse.compareInclusionSizeCell(SvgCellRepet20, SvgCellEval20, cellRepet180, cellEval180, nameTest, ech);
				}
				// fractal dimention calculation
				int resolution = 10;
				// pour seulement 20 valeures
				for (File f : anal.getRandomSeedScenars(arL.get(0), echelle, 1)) {
					FractalDimention.getCorrFracDim(getBuild(fileDonnee, arL), f, statFile, resolution, f.getName());
				}
			}
		}
		return resultFile;
	}

	public static File runStabAHP(File file, File fileDonnee, String name, boolean machineReadable) throws Exception {
		System.out.println("Initialization");
		Initialize.init();
		// folder settings
		File resultFile = new File(file, "result--" + name);
		if (machineReadable) {
			resultFile = new File(file.getParentFile(), "result--" + name);
		}
		resultFile.mkdir();
		RasterAnalyse.rootFile = file;
		RasterAnalyse.stabilite = true;

		Analyse anal = new Analyse(file, name);
		System.out.println(anal.ahpCollec);
		// sélectionne des listes de scénario ayant pour différence la seed
		for (List<ScenarAnalyse> arL : anal.getScenarDiffSeed()) {
			System.out.println("on trouves " + arL.get(0).getScenarFile());
			// pour tous les fichiers de ces listes

			// pour les trois premières échelles
			for (String echelle : anal.getEchelleRange(3, arL.get(0))) {
				System.out.println("!! Pour les scénarios " + arL.get(0).getNiceNameWthSeed());

				// convert Scenar to File
				String nameTest = arL.get(0).getProjFile().getName() + "_" + arL.get(0).getnMax() + "_" + arL.get(0).isStrict() + "_" + arL.get(0).isYag() + "_"
						+ arL.get(0).getAhp();

				if (machineReadable) {
					nameTest = new String(arL.get(0).getNiceNameWthSeed());
				}

				//
				File eachResultFile = new File(resultFile, nameTest);
				eachResultFile.mkdirs();

				File statFile = new File(eachResultFile, "stat");
				RasterAnalyse.statFile = statFile;
				File rastFile = new File(eachResultFile, "raster");
				rastFile.mkdir();

				RasterAnalyse.echelle = echelle;
				List<File> fileToTest = new ArrayList<File>();

				// get the set of files to test
				for (ScenarAnalyse sC : arL) {

					fileToTest.add(anal.getSimuFile(sC, echelle, "evalAnal"));
				}

				// merge the different input rasters
				RasterMergeResult mergedResult = RasterAnalyse.mergeRasters(fileToTest);

				// statistics for the simple task with those objects
				RasterAnalyse.createStatsDescriptive(nameTest, mergedResult);
				RasterAnalyse.createStatsEvol(mergedResult.getHisto(), echelle);

				// get the average evaluation of cells in a .csv
				if (!mergedResult.getCellEval().isEmpty()) {
					RasterAnalyse.createStatEvals(mergedResult.getCellEval());
				}

				// create a merged raster
				RasterMerge.merge(fileToTest, new File(rastFile, nameTest + "-rasterMerged-" + echelle + ".tif"), Integer.parseInt(echelle));
				// fractal dimention calculation
				int resolution = 10;
				// pour seulement 20 valeures
				for (File f : anal.getRandomSeedScenars(arL.get(0), echelle, 1)) {
					FractalDimention.getCorrFracDim(getBuild(fileDonnee, arL), f, statFile, resolution, f.getName());
				}
			}
		}
		return resultFile;
	}

	/**
	 * Overlaoding to use aggregation transition from openMole Creates a new directory <i>mainFile</i> with the use of <i>copyToScenVrac</i> and puts all of the incoming
	 * <i>file[]</i> in it
	 * 
	 * @param file
	 *            : the array file contaning the different scenarios
	 * @param fileDonnee
	 *            : file containing the root data
	 * @param name
	 *            : name of the explo
	 * @param machineReadable
	 *            : if it's set to be launch by the machine (openmole)
	 * @return File : the result file
	 * @throws Exception
	 */
	public static File runCompData(File[] file, File[] fileDonnee, File mainFile, String[] name, boolean machineReadable) throws Exception {
		return runCompData(copyToScenVrac(file, mainFile), fileDonnee[0], name[0], machineReadable);
	}

	/**
	 * Fonction permettant de comparer les différents sets de données décris dans la partie 2.6.1 de ma thèse
	 * 
	 * @param file
	 * @param buildFile
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public static File runCompData(File file, File fileDonnee, String name, boolean machineReadable) throws Exception {

		RasterAnalyse.rootFile = file;

		File resultFile = new File(file, "result--" + name);
		if (machineReadable) {
			resultFile = new File(file.getParentFile(), "result--" + name);
		}
		resultFile.mkdir();
		File discreteFile = getDiscrete(fileDonnee);
		Analyse compDonnee = new Analyse();
		if (machineReadable) {
			compDonnee = new Analyse(file, name, machineReadable);
		} else {
			compDonnee = new Analyse(file, name);
		}

		// définition des pairs que l'on veut analyser
		List<MutablePair<String, String>> listPair = new ArrayList<>();
		MutablePair<String, String> pair = new MutablePair<String, String>("Manuel", "RouteAutom");
		listPair.add(pair);
		pair = new MutablePair<String, String>("Manuel", "BatiAutom");
		listPair.add(pair);
		pair = new MutablePair<String, String>("Manuel", "TransportAutom");
		listPair.add(pair);
		pair = new MutablePair<String, String>("Manuel", "LoisirAutom");
		listPair.add(pair);
		pair = new MutablePair<String, String>("Manuel", "ServiceAutom");
		listPair.add(pair);
		pair = new MutablePair<String, String>("Manuel", "LoisirServiceAutom");
		listPair.add(pair);
		pair = new MutablePair<String, String>("Autom", "LoisirManu");
		listPair.add(pair);
		pair = new MutablePair<String, String>("Autom", "ServiceManu");
		listPair.add(pair);
		pair = new MutablePair<String, String>("Autom", "LoisirServiceManu");
		listPair.add(pair);

		// pour toutes ces pairs
		for (MutablePair<String, String> zePair : listPair) {
			// on créée des listes de scénarios
			Set<ScenarAnalyse> firstSc = new HashSet<>();
			Set<ScenarAnalyse> secSc = new HashSet<>();
			for (ScenarAnalyse scenar : compDonnee.scenarCollec) {
				if (scenar.getData().equals(zePair.getLeft())) {
					firstSc.add(scenar);
				} else if (scenar.getData().equals(zePair.getRight())) {
					secSc.add(scenar);
				}
			}
			try {
				for (ScenarAnalyse fSc : firstSc) {
					for (ScenarAnalyse sSc : secSc) {
						if (fSc.getShortScenarName().equals(sSc.getShortScenarName())) {
							for (String echelle : compDonnee.getEchelleRange(3, fSc)) {
								System.out.println("comparaison des données " + fSc.getData() + " et " + sSc.getData() + " pour l'échelle " + echelle);
								List<File> fileToTest = new ArrayList<>();
								// get the file from the first scenar selected with the right echelle and
								// meaning
								fileToTest.add(compDonnee.getSimuFile(sSc, echelle, "evalAnal"));
								// get the file from the second scenar selected with the right echelle and
								// meaning
								fileToTest.add(compDonnee.getSimuFile(fSc, echelle, "evalAnal"));

								System.out.println("les scenars comparés sont : ");
								for (File f : fileToTest) {
									System.out.println(f);
								}
								RasterAnalyse.echelle = echelle;
								RasterMergeResult result = RasterAnalyse.mergeRasters(fileToTest);
								String nameComp = fSc.getData() + "-CompareTo-" + sSc.getData();
								// statistics
								File statFile = new File(resultFile, "stat-" + nameComp);
								RasterAnalyse.statFile = statFile;
								RasterAnalyse.createStatsDescriptive(name + "-" + echelle + sSc.getShortScenarName(), result);
								RasterMerge.merge(fileToTest, new File(resultFile, nameComp + "-Scenar" + sSc.getShortScenarName() + "-rasterMerged-" + echelle + ".0.tif"),
										Integer.valueOf(echelle));
								RasterAnalyse.createStatsStabDiscrete(name + "-" + echelle + "-Scenar" + sSc.getShortScenarName(), result, discreteFile);
							}
						}
					}
				}
			} catch (NullPointerException n) {
			}
		}
		return resultFile;
	}

	public static File getEvalTotal(File exampleFile, int ech) throws FileNotFoundException {
		for (File f : exampleFile.listFiles()) {
			if (f.getName().endsWith("eval-" + ech + ".0.tif")) {
				return f;
			}
		}
		throw new FileNotFoundException("Example file not found");
	}

	public static File copyToScenVrac(File[] file, File mainFile) throws IOException {
		File fileVrac = new File(mainFile, "ScenarVrac");
		fileVrac.mkdirs();
		List<Path> vreListFiles = new ArrayList<Path>();
		for (int i = 0; i < file.length; i++) {
			for (File f : file[i].listFiles()) {
				if (f.toString().endsWith(".tif") || f.toString().endsWith(".tfw")) {
					vreListFiles.add(Paths.get(f.toString()));
				}
			}
		}
		for (Path p : vreListFiles) {
			System.out.println("copy " + fileVrac + "/" + p.getFileName().toString());
			OutputStream out = new FileOutputStream(new File(fileVrac, p.getFileName().toString()));
			Files.copy(p, out);
			out.close();
		}
		return fileVrac;
	}

	/**
	 * select a scaled sample of an eval-anal type output
	 * 
	 * @param exampleFolder
	 *            : where to get the sample
	 * @param ech
	 *            : scale
	 * @return the sample
	 * @throws IOException
	 */
	public static File getOutputExample(File exampleFolder, int ech) throws IOException {
		// copy an example of output
		for (File f : exampleFolder.listFiles()) {
			if (f.getName().endsWith("evalAnal-" + ech + ".0.tif")) {
				return f;
			}
		}
		throw new FileNotFoundException("Example file not found");
	}

	public static File getDiscrete(File fileDonnee) {
		File discreteFile = new File("");
		for (File filesDonnee : fileDonnee.listFiles()) {
			try {
				for (File fileShp : filesDonnee.listFiles()) {
					if (fileShp.getName().equals("discreteFile.shp")) {
						discreteFile = fileShp;
						break;
					}
				}
			} catch (NullPointerException e) { // Si les données sont toutes dans un unique répertoire
				if (filesDonnee.getName().equals("discreteFile.shp")) {
					discreteFile = filesDonnee;
					break;
				}
			}
		}
		return discreteFile;
	}

	public static File getBuild(File fileDonnee, List<ScenarAnalyse> arL) {
		System.out.println("getBuild from " + fileDonnee + " with " + arL.get(0).getData());
		File batiFile = new File("");
		// for (File filesDonnee : fileDonnee.listFiles()) {
		// if (filesDonnee.getName().endsWith(arL.get(0).getData())) {
		// System.out.println("searching " + filesDonnee);
		// for (File fileShp : filesDonnee.listFiles()) {
		// if (fileShp.getName().startsWith("batiment") &&
		// fileShp.toString().endsWith(".shp")) {
		// batiFile = fileShp;
		// }
		// }
		// }
		// }
		for (File fileShp : fileDonnee.listFiles()) {
			if (fileShp.getName().startsWith("batiment") && fileShp.toString().endsWith(".shp")) {
				batiFile = fileShp;
			}
		}
		return batiFile;
	}

	/**
	 * copy an example of scenar output folder
	 * 
	 * @param resultFile
	 *            : the file where the example will be copied
	 * @param folderToCopy
	 *            : the folder to copy
	 * @return the copied file
	 * @throws IOException
	 */
	public static File copyExample(File resultFile, List<File> list) throws IOException {
		File exampleFile = new File(resultFile, "SortieExemple");
		exampleFile.mkdir();

		for (File f : list) {
			FileOutputStream out = new FileOutputStream(new File(exampleFile, f.getName()));
			Files.copy(f.toPath(), out);
		}
		return exampleFile;
	}

	public static File copyExample(File resultFile, ScenarAnalyse sC) throws IOException {
		List<File> list = new ArrayList<File>();
		list.add(sC.getScenarFile());
		return copyExample(resultFile, list);
	}

	// public static void copyFolder(Path logFile) {
	//
	// try (BufferedWriter writer = Files.newBufferedWriter(logFile,
	// StandardCharsets.UTF_8, StandardOpenOption.WRITE)) { // buffer en ecriture
	// (ecrase l’existant), encodage UTF8
	//
	// writer.write("Hello World!\n");
	// for (int i = 100; i > 0; --i) {
	//
	// String n = "" + i + "\n";
	// writer.write(n);
	//
	// }
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }

}
