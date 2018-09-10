package fr.ign.analyse.obj;

import java.io.File;

public class ScenarAnalyseFile extends ScenarAnalyse {

	public String echelle;
	public File filescenarfile ;
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((echelle == null) ? 0 : echelle.hashCode());
		result = prime * result + ((meaning == null) ? 0 : meaning.hashCode());
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
		ScenarAnalyseFile other = (ScenarAnalyseFile) obj;
		if (echelle == null) {
			if (other.echelle != null)
				return false;
		} else if (!echelle.equals(other.echelle))
			return false;
		if (meaning == null) {
			if (other.meaning != null)
				return false;
		} else if (!meaning.equals(other.meaning))
			return false;
		return true;
	}

	public String meaning;

	public ScenarAnalyseFile(boolean machineRead, File fileProj, File fileScenar, String sizeCell2, String grid2, String seuil2, String data2, String nMax2, String ahp2,
			String strict2, String yag2, String seed2, String echl, String mean) {
		super(machineRead, fileProj, fileScenar, sizeCell2, grid2, seuil2, data2, nMax2, ahp2, strict2, yag2, seed2);
		echelle = echl;
		meaning = mean;
	}
	
	public ScenarAnalyseFile(boolean machineRead, File fileProj, File fileScenar,File fileScenarFile, String sizeCell2, String grid2, String seuil2, String data2, String nMax2, String ahp2,
			String strict2, String yag2, String seed2, String echl, String mean) {
		super(machineRead, fileProj, fileScenar, sizeCell2, grid2, seuil2, data2, nMax2, ahp2, strict2, yag2, seed2);
		filescenarfile = fileScenarFile;
		echelle = echl;
		meaning = mean;
	}

	@Override
	public String getNiceName() {
		return super.getNiceName() + "-" + meaning + "-" + echelle + ".0.tif";
	}

	public String getScenarName() {
		return super.getNiceName();
	}

	public String getEchelle() {
		return echelle;
	}

	public String getMeaning() {
		return meaning;
	}

	public File getFileFile() {
		File resultFile = new File("");
		if (machineReading) {
			resultFile = scenarFile;
		} else {
			resultFile = filescenarfile;
		}
		return resultFile;
	}

	@Override
	public String toString() {
		return "ScenarAnalyseFile [echelle=" + echelle + ", meaning=" + meaning + ", nMax=" + nMax + ", ahp=" + ahp + ", strict=" + strict + ", yag=" + yag + ", scenarFile="
				+ scenarFile + ", seed=" + seed + ", projFile=" + projFile + ", sizeCell=" + sizeCell + ", grid=" + grid + ", seuil=" + seuil + ", data=" + data
				+ ", machineReading=" + machineReading + "]";
	}
}
