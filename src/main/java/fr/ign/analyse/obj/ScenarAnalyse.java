package fr.ign.analyse.obj;

import java.io.File;
import java.util.regex.Pattern;

public class ScenarAnalyse extends ProjetAnalyse {

	/**
	 * Set the info needed for a general project :
	 * 
	 * @param projfile
	 * @param sizeCell2
	 * @param grid2
	 * @param seuil2
	 * @param data2
	 *            and a single scenario
	 * @param nMax2
	 * @param ahp2
	 * @param strict2
	 * @param yag2
	 * @param fileName2
	 * @param seed2
	 */
	public ScenarAnalyse(boolean machineRead, File projfile, File scenarfile, String sizeCell2, String grid2, String seuil2, String data2, String nMax2, String ahp2,
			String strict2, String yag2, String seed2) {
		super(machineRead, projfile, sizeCell2, grid2, seuil2, data2);
		nMax = nMax2;
		ahp = ahp2;
		strict = strict2;
		yag = yag2;
		seed = seed2;
		scenarFile = scenarfile;
	}

	/**
	 * Lazy way to determine MupCity's characteristics for scenarios
	 * @param sizeCell2
	 * @param grid2
	 * @param seuil2
	 * @param data2
	 * @param nMax2
	 * @param ahp2
	 * @param strict2
	 * @param yag2
	 * @param seed2
	 */
	public ScenarAnalyse(String sizeCell2, String grid2, String seuil2, String data2, String nMax2, String ahp2,
			String strict2, String yag2, String seed2) {
		super(sizeCell2, grid2, seuil2, data2);
		nMax = nMax2;
		ahp = ahp2;
		strict = strict2;
		yag = yag2;
		seed = seed2;
	}
	
	/**
	 * Lazy constructor
	 * @param totFile
	 */
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
		scenarFile = totFile;
	}

	protected String nMax;
	protected String ahp;
	protected String strict;
	protected String yag;
	protected File scenarFile;
	protected String seed;

	@Override
	public String getNiceName() {
		return super.getNiceName() + "--" + nMax + "_" + strict + "_" + yag + "_" + ahp + "_seed_" + seed;
	}

	public String getNiceNameWthSeed() {
		return super.getNiceName() + "--" + nMax + "_" + strict + "_" + yag + "_" + ahp ;
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

	public File getScenarFile() {
		return scenarFile;
	}

	public String getScenarName() {
		return scenarFile.getName();
	}

	public String getShortScenarName() {
		return nMax + "_" + strict + "_" + yag + "_" + ahp + "_seed_" + seed;
	}
	public String getShortScenarNameWthSeed() {
		return nMax + "_" + strict + "_" + yag + "_" + ahp;
	}

	public String getSeed() {
		return seed;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((ahp == null) ? 0 : ahp.hashCode());
		result = prime * result + ((nMax == null) ? 0 : nMax.hashCode());
		result = prime * result + ((seed == null) ? 0 : seed.hashCode());
		result = prime * result + ((strict == null) ? 0 : strict.hashCode());
		result = prime * result + ((yag == null) ? 0 : yag.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ScenarAnalyse other = (ScenarAnalyse) obj;
		if (ahp == null) {
			if (other.ahp != null)
				return false;
		} else if (!ahp.equals(other.ahp))
			return false;
		if (nMax == null) {
			if (other.nMax != null)
				return false;
		} else if (!nMax.equals(other.nMax))
			return false;
		if (seed == null) {
			if (other.seed != null)
				return false;
		} else if (!seed.equals(other.seed))
			return false;
		if (strict == null) {
			if (other.strict != null)
				return false;
		} else if (!strict.equals(other.strict))
			return false;
		if (yag == null) {
			if (other.yag != null)
				return false;
		} else if (!yag.equals(other.yag))
			return false;
		return true;
	}

	public boolean sameScenario(ScenarAnalyse sA) {
		return nMax.equals(sA.getnMax()) && ahp.equals(sA.getAhp()) && strict.equals(sA.isStrict()) && yag.equals(sA.isYag()) && seed.equals(sA.getSeed());
	}

	@Override
	public String toString() {
		return "ScenarAnalyse [nMax=" + nMax + ", ahp=" + ahp + ", strict=" + strict + ", yag=" + yag + ", scenarFile=" + scenarFile + ", seed=" + seed + ", projFile=" + projFile
				+ ", sizeCell=" + sizeCell + ", grid=" + grid + ", seuil=" + seuil + ", data=" + data + "]";
	}
}
