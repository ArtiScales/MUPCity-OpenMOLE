package fr.ign.task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.lang3.tuple.MutablePair;
import org.geotools.geometry.DirectPosition2D;

import fr.ign.analyse.FractalDimention;
import fr.ign.analyse.RasterAnalyse;
import fr.ign.analyse.RasterMerge;
import fr.ign.analyse.RasterMergeResult;
import fr.ign.analyse.obj.Analyse;
import fr.ign.analyse.obj.ScenarAnalyse;

public class AnalyseTask {

	// public static String echelle;

	public static void main(String[] args) throws Exception {
		// File file = new File("/home/mcolomb/workspace/mupcity-openMole/result/gridExploProjets2");
		// runGridSens(file, new File("/home/mcolomb/workspace/mupcity-openMole/data/"), "gridExplo");

		// File file = new File("/home/mcolomb/workspace/mupcity-openMole/result/brandNewStab/ScenarVrac");
		// System.out.println(runStab(file, new File("/home/mcolomb/workspace/mupcity-openMole/data/"), "stability", true));

		File file = new File("/home/mcolomb/workspace/mupcity-openMole/result/compDonneeTest/ScenarVrac");
		runCompData(file, new File("/home/mcolomb/workspace/mupcity-openMole/data/"), "compDonnee", true);

	}

	public static File runGridSens(File[] file, File[] fileDonnee, File mainFile, String[] name, boolean machineReadable) throws Exception {

		return runGridSens(copyToScenVrac(fileDonnee, mainFile), fileDonnee[0], name[0], machineReadable);
	}

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
	public static File runGridSens(File file, File fileDonnee, String name, boolean machineReadable) throws Exception {

		RasterAnalyse.rootFile = file;
		RasterAnalyse.cutBorder = true;

		File discreteFile = getDiscrete(fileDonnee);

		File resultFile = new File(file, "result-" + name);
		if (machineReadable) {
			resultFile = new File(file.getParentFile(), "result--" + name);
		}

		File rastFile = new File(resultFile, "raster");

		Analyse anal = new Analyse();
		if (machineReadable) {
			anal = new Analyse(file, name, machineReadable);
		} else {
			anal = new Analyse(file, name);
		}

		// compare the effect of the minimal sizes of the cells
		List<List<ScenarAnalyse>> listCellMin = anal.getScenars(anal.getProjetByCellmin());

		for (List<ScenarAnalyse> list : listCellMin) {
			File statFile = new File(resultFile, "stat-" + list.get(0).getProjFile().getName() + "-" + list.get(0).getScenarName());
			statFile.mkdirs();
			RasterAnalyse.statFile = statFile;

			// create analysis of different parameter setting regarding to different size of cells
			RasterAnalyse.compareDiffSizedCell(list, name, discreteFile);
		}

		// compare les réplication entre les seuils

		List<List<ScenarAnalyse>> listsSeuil = anal.getProjetBySeuil();

		for (List<ScenarAnalyse> listSeuil : listsSeuil) {
			File statFile = new File(resultFile, "stat-" + listSeuil.get(0).getSeuil() + "-" + listSeuil.get(0).getGrid() + "-" + listSeuil.get(0).getScenarName());
			statFile.mkdirs();
			RasterAnalyse.statFile = statFile;

			RasterMergeResult resultMergedSeuil = RasterAnalyse.mergeRasters(listSeuil, listSeuil.get(0));
			RasterAnalyse.createStatsDescriptive(name + ("SeuilComparison"), resultMergedSeuil);
			// don't know why this one crashes coz of a lock
			RasterAnalyse.createStatsDiscrete(name + ("SeuilComparisonDiscrete"), resultMergedSeuil, discreteFile);
			rastFile.mkdirs();
			RasterMerge.merge(listSeuil,
					new File(rastFile,
							"rasterMerged-SeuilComp" + listSeuil.get(0).getSizeCell() + "-" + listSeuil.get(0).getSeuil() + "-" + listSeuil.get(0).getScenarName() + ".tif"),
					RasterAnalyse.cutBorder);
		}

		// compare les réplication entre les grilles

		System.out.println("------------(((Grid)))------------");
		List<List<ScenarAnalyse>> listsGrid = anal.getProjetByGrid();

		for (List<ScenarAnalyse> listGrid : listsGrid) {
			File statFile = new File(resultFile, "stat-" + listGrid.get(0).getSizeCell() + "-" + listGrid.get(0).getSeuil() + "-" + listGrid.get(0).getScenarName());
			statFile.mkdirs();
			RasterAnalyse.statFile = statFile;

			RasterMergeResult resultMergedSeuil = RasterAnalyse.mergeRasters(listGrid, listGrid.get(0));
			RasterAnalyse.createStatsDescriptive(name + ("GridComparison"), resultMergedSeuil);
			RasterAnalyse.createStatsDiscrete(name + ("GridComparisonDiscrete"), resultMergedSeuil, discreteFile);
			rastFile.mkdirs();
			RasterMerge.merge(listGrid,
					new File(rastFile,
							"rasterMerged-SeuilComp" + listGrid.get(0).getSizeCell() + "-" + listGrid.get(0).getSeuil() + "-" + listGrid.get(0).getScenarName() + ".tif"),
					RasterAnalyse.cutBorder);
		}

		// test the effect of grid - compare les réplication entre les seuils et les différentes grilles

		return resultFile;
	}

	/**
	 * overlaoding to use aggregation transition from openMole
	 * 
	 * @param file
	 * @param fileDonnee
	 * @param name
	 * @param machineReadable
	 * @return
	 * @throws Exception
	 */
	public static File runStab(File[] file, File[] fileDonnee, File mainFile, String[] name, boolean machineReadable) throws Exception {
		return runStab(copyToScenVrac(fileDonnee, mainFile), fileDonnee[0], name[0], machineReadable);
	}

	public static File runStab(File file, File fileDonnee, String name, boolean machineReadable) throws Exception {

		File discreteFile = getDiscrete(fileDonnee);
		System.out.println("root " + file);
		// folder settings

		File resultFile = new File(file, "result--" + name);
		resultFile.mkdir();
		if (machineReadable) {
			resultFile = new File(file.getParentFile(), "result--" + name);
		}

		RasterAnalyse.rootFile = file;
		RasterAnalyse.stabilite = true;

		// toutes les listes des projets à tester
		Analyse anal = new Analyse();
		if (machineReadable) {
			anal = new Analyse(file, name, machineReadable);
		} else {
			anal = new Analyse(file, name);
		}

		for (List<ScenarAnalyse> arL : anal.getScenarDiffSeed()) {

			if (arL.get(0).getMeaning().equals("eval_anal")) {

				int minSizeCell = Integer.valueOf(arL.get(0).getSizeCell());

				for (ScenarAnalyse scen : arL) {
					if (minSizeCell > Integer.valueOf(scen.getSizeCell())) {
						minSizeCell = Integer.valueOf(scen.getSizeCell());
					}
				}

				for (int ech = minSizeCell; ech <= minSizeCell * 9; ech = ech * 3) {

					String echelle = String.valueOf(ech);

					if (!(echelle.equals(arL.get(0).getEchelle()) && arL.get(0).getMeaning().equals("eval_anal"))) {
						break;
					}

					// convert Scenar to File
					String nameTest = arL.get(0).getProjFile().getName() + "_" + arL.get(0).getnMax() + "_" + arL.get(0).isStrict() + "_" + arL.get(0).isYag() + "_"
							+ arL.get(0).getAhp();

					if (machineReadable) {
						nameTest = new String(arL.get(0).getNiceName());
					}

					File eachResultFile = new File(resultFile, nameTest);
					eachResultFile.mkdirs();

					File statFile = new File(eachResultFile, "stat");
					RasterAnalyse.statFile = statFile;
					File rastFile = new File(eachResultFile, "raster");
					rastFile.mkdir();

					File exampleFolder = new File("");
					if (machineReadable) {
						exampleFolder = copyExample(eachResultFile, anal.getSimuFiles(arL.get(0)));
					} else {
						exampleFolder = copyExample(eachResultFile, arL.get(0));
					}
					RasterAnalyse.echelle = echelle;
					List<File> fileToTest = new ArrayList<File>();

					// get the set of files to test
					for (ScenarAnalyse sc : arL) {
						fileToTest.add(sc.getSimuFile(echelle));
					}
					fileToTest.remove(null);

					// merge the different input rasters
					RasterMergeResult mergedResult = RasterAnalyse.mergeRasters(fileToTest);

					// statistics for the simple task with those objects
					RasterAnalyse.createStatsDescriptive(nameTest, mergedResult);
					RasterAnalyse.createStatsEvol(mergedResult.getHisto(), echelle);

					File evalTotal = new File("");

					// get eval total
					for (File f : eachResultFile.listFiles()) {
						if (f.getName().endsWith("eval-" + echelle + ".0.tif")) {
							evalTotal = f;
						}
					}

					RasterAnalyse.createStatEvals(mergedResult.getCellEval(), evalTotal);

					// discrete statistics

					RasterAnalyse.createStatsDiscrete(nameTest, mergedResult, discreteFile);

					// create a merged raster
					RasterMerge.merge(fileToTest, new File(rastFile, nameTest + "-rasterMerged-" + echelle + ".tif"), Integer.parseInt(echelle));

					// cells contained
					// reference simulation
					File concernedFile = getOutputExample(exampleFolder, ech);
					// Count how much 20m cells are contained into parent cells
					Hashtable<DirectPosition2D, Float> SvgCellEval20 = new Hashtable<DirectPosition2D, Float>();
					Hashtable<DirectPosition2D, Integer> SvgCellRepet20 = new Hashtable<DirectPosition2D, Integer>();
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
					int resolution = 4;
					FractalDimention.getCorrFracDimfromSimu(getBuild(fileDonnee, arL), file, statFile, echelle, resolution);

				}
			}
		}
		return resultFile;
	}

	/**
	 * overlaoding to use aggregation transition from openMole
	 * 
	 * @param file
	 * @param fileDonnee
	 * @param name
	 * @param machineReadable
	 * @return
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

		for (MutablePair<String, String> zePair : listPair) {
			List<ScenarAnalyse> firstSc = new ArrayList<>();
			List<ScenarAnalyse> secSc = new ArrayList<>();
			for (List<ScenarAnalyse> scenars : compDonnee.getScenars()) {
				for (ScenarAnalyse scenar : scenars) {
					if (scenar.getData().equals(zePair.getLeft())) {
						firstSc.add(scenar);
					} else if (scenar.getData().equals(zePair.getRight())) {
						secSc.add(scenar);
					}
				}
			}
			try {
				for (ScenarAnalyse f : firstSc) {
					for (ScenarAnalyse ff : secSc) {
						int minSizeCell = Integer.valueOf(f.getSizeCell());
						// for each scale
						for (int ech = minSizeCell; ech <= minSizeCell * 9; ech = ech * 3) {

							if (f.getMeaning().equals("eval_anal") && f.getEchelle().equals(String.valueOf(ech)) && ff.getMeaning().equals("eval_anal")
									&& ff.getEchelle().equals(String.valueOf(ech))) {
								String echelle = String.valueOf(ech);
								RasterAnalyse.echelle = echelle;

								List<File> fileToTest = new ArrayList<>();
								fileToTest.add(f.getSimuFile(echelle));
								fileToTest.add(ff.getSimuFile(echelle));
								RasterMergeResult result = RasterAnalyse.mergeRasters(fileToTest);

								String nameComp = f.getData() + "-CompareTo-" + ff.getData();
								// statistics
								File statFile = new File(resultFile, "stat-" + nameComp);
								RasterAnalyse.statFile = statFile;

								RasterAnalyse.createStatsDescriptive(name + "-" + echelle, result);
								RasterMerge.merge(fileToTest, new File(resultFile, nameComp + "-rasterMerged-" + ech + ".0.tif"), ech);
								RasterAnalyse.createStatsDiscrete(name + "-" + echelle, result, discreteFile);
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

	private static File copyToScenVrac(File[] file, File mainFile) throws IOException {
		File fileVrac = new File(mainFile, "ScenarVrac");
		fileVrac.mkdirs();
		List<Path> vreListFiles = new ArrayList<Path>();
		for (int i = 0; i < file.length; i++) {
			for (File f : file[i].listFiles()) {
				if (f.toString().endsWith(".tif")) {
					vreListFiles.add(Paths.get(f.toString()));
				}
			}
		}
		for (Path p : vreListFiles) {
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
			if (f.getName().endsWith("eval_anal-" + ech + ".0.tif")) {
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
		File batiFile = new File("");
		for (File filesDonnee : fileDonnee.listFiles()) {
			if (filesDonnee.toString().equals(arL.get(0).getData())) {
				for (File fileShp : filesDonnee.listFiles()) {
					if (fileShp.toString().startsWith("batiment")) {
						batiFile = fileShp;
					}
				}
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
		list.add(sC.getFolderName());
		return copyExample(resultFile, list);
	}

	public static void copyFolder(Path logFile) {

		try (BufferedWriter writer = Files.newBufferedWriter(logFile, StandardCharsets.UTF_8, StandardOpenOption.WRITE)) { // buffer en ecriture (ecrase l’existant), encodage UTF8

			writer.write("Hello World!\n");
			for (int i = 100; i > 0; --i) {

				String n = "" + i + "\n";
				writer.write(n);

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
