package fr.ign.analyse.obj;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.regex.Pattern;

public class ScenarAnalyse extends ProjetAnalyse {
	/**
	 * constructor for machineReader analysis
	 * 
	 * @param sizeCell2
	 * @param grid2
	 * @param seuil2
	 * @param data2
	 * @param nMax2
	 * @param ahp2
	 * @param strict2
	 * @param yag2
	 * @param fileName2
	 * @param seed2
	 * @param echl
	 * @param meanig
	 */
	public ScenarAnalyse(String sizeCell2, String grid2, String seuil2, String data2, String nMax2, String ahp2, String strict2, String yag2, File fileName2, String seed2,
			String echl, String meanig) {
		super(fileName2, sizeCell2, grid2, seuil2, data2);
		nMax = nMax2;
		ahp = ahp2;
		strict = strict2;
		yag = yag2;
		seed = seed2;
		folderName = fileName2;

		// scenar in the same foldrer - special tasks
		machineReading = true;

		echelle = echl;
		meaning = meanig;
	}

	public ScenarAnalyse(File projfile, String sizeCell2, String grid2, String seuil2, String data2, String nMax2, String ahp2, String strict2, String yag2, File fileName2,
			String seed2) {
		super(projfile, sizeCell2, grid2, seuil2, data2);

		nMax = nMax2;
		ahp = ahp2;
		strict = strict2;
		yag = yag2;
		seed = seed2;
		folderName = fileName2;
	}

	public ScenarAnalyse(File totFile) {
		super(totFile);

		Pattern dbTiret = Pattern.compile("--");
		Pattern tiret = Pattern.compile("-");
		Pattern t8 = Pattern.compile("_");

		String[] decompName = dbTiret.split(totFile.getName());
		String scenarName = decompName[1];
		String[] decompScen = t8.split(scenarName);
		String[] decompSeed = tiret.split(decompScen[5]);
		nMax = decompScen[0];
		ahp = decompScen[3];
		strict = decompScen[1];
		yag = decompScen[2];
		seed = decompSeed[0];
		folderName = totFile;
	}

	private String nMax;
	private String ahp;
	private String strict;
	private String yag;
	private File folderName;
	private String seed;
	private boolean machineReading;
	private String echelle;
	private String meaning;

	@Override
	public String getNiceName() {
		return super.getNiceName() + "--" + nMax + "_" + strict + "_" + yag + "_" + ahp + "_seed_" + seed;
	}

	public String getEchelle() {
		return echelle;
	}

	public String getnMax() {
		return nMax;
	}

	public String getAhp() {
		return ahp;
	}

	public String isStrict() {
		return strict;
	}

	public String isYag() {
		return yag;
	}

	public File getFolderName() {
		return folderName;
	}

	public String getScenarName() {
		return folderName.getName();
	}

	public String getSeed() {
		return seed;
	}

	public File getSimuFile() throws FileNotFoundException {
		return getSimuFile("eval_anal", sizeCell);
	}

	public File getSimuFile(String echelle) throws FileNotFoundException {
		return getSimuFile("eval_anal", echelle);
	}

	public File getSimuFile(String type, String ech) throws FileNotFoundException {
		if (machineReading) {
			if (type.equals(meaning) && ech.equals(echelle)) {
				return folderName;
			} else {
				return null;
			}
		} else {
			for (File f : folderName.listFiles()) {
				if (f.getName().endsWith("eval_anal-" + echelle + ".0.tif")) {
					return f;
				}
			}
		}
		throw new FileNotFoundException("Scenar file not found or not ");
	}

	public boolean equals(ScenarAnalyse sA) {
		boolean isEq = false;
		if (nMax.equals(sA.getnMax()) && ahp.equals(sA.getAhp()) && strict.equals(sA.isStrict()) && yag.equals(sA.isYag()) && seed.equals(sA.getSeed())) {
			isEq = true;
		}
		return isEq;
	}

	public String getMeaning() {
		return meaning;
	}
}
