package fr.ign.analyse.obj;

import java.io.File;
import java.util.regex.Pattern;

public class ProjetAnalyse {

	protected File projFile;
	protected String sizeCell;
	protected String grid;
	protected String seuil;
	protected String data;
	protected boolean machineReading;

	public ProjetAnalyse(boolean machineRead,File projFile2, String sizeCell2, String grid2, String seuil2, String data2) {
		machineReading = machineRead;
		projFile = projFile2;
		sizeCell = sizeCell2;
		grid = grid2;
		seuil = seuil2;
		data = data2;
	}
/**
 * Lazy way to determine MupCity's characteristics
 * @param sizeCell2
 * @param grid2
 * @param seuil2
 * @param data2
 */
	public ProjetAnalyse(String sizeCell2, String grid2, String seuil2, String data2) {
		sizeCell = sizeCell2;
		grid = grid2;
		seuil = seuil2;
		data = data2;
		}

	public ProjetAnalyse(File totFile) {
		Pattern tiret = Pattern.compile("-");
		String[] decompName = tiret.split(totFile.getName());
		projFile = totFile;
		sizeCell = decompName[2].replace("CM", "").replace(".0", "");
		grid = decompName[4].replace("GP_", "");
		seuil = decompName[3].replace("S", "");
		data = decompName[1];
	}


	public File getProjFile() {
		return projFile;
	}

	public String getData() {
		return data;
	}

	public String getSizeCell() {
		return sizeCell;
	}

	public String getGrid() {
		return grid;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((grid == null) ? 0 : grid.hashCode());
		result = prime * result + ((seuil == null) ? 0 : seuil.hashCode());
		result = prime * result + ((sizeCell == null) ? 0 : sizeCell.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProjetAnalyse other = (ProjetAnalyse) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (grid == null) {
			if (other.grid != null)
				return false;
		} else if (!grid.equals(other.grid))
			return false;
		if (seuil == null) {
			if (other.seuil != null)
				return false;
		} else if (!seuil.equals(other.seuil))
			return false;
		if (sizeCell == null) {
			if (other.sizeCell != null)
				return false;
		} else if (!sizeCell.equals(other.sizeCell))
			return false;
		return true;
	}


	public String getSeuil() {
		return seuil;
	}
	public String getNiceName(){
		return data+"-CM"+sizeCell+".0-S"+seuil+"-GP_"+grid;
	}


	public boolean sameProjet(ScenarAnalyse sA) {

		return seuil.equals(sA.getSeuil()) && data.equals(sA.getData()) && sizeCell.equals(sA.getSizeCell()) && grid.equals(sA.getGrid()) ;
	}

//	public boolean equals(ProjetAnalyse pA) {
//		return grid.equals(pA.getGrid()) && seuil.equals(pA.getSeuil())
//		boolean isEq = false;
//		if (grid.equals(pA.getGrid())) {
//			if (seuil.equals(pA.getSeuil())) {
//				if (sizeCell.equals(pA.getSizeCell())) {
//					if (data.equals(pA.getData())) {
//						isEq = true;
//					}
//				}
//			}
//		}
//		return isEq;
//	}

}
