package fr.ign.tools;

import java.io.FileNotFoundException;
import java.util.HashMap;

public class ScenarTools {

	/**
	 * determines automaticly wether the simulation is using Yager or Moyenne pondéré aggregation methods
	 * 
	 * @param list
	 *            : A hashMap containing the ponderation. Name of the ponderation must have the value 99.0.
	 * @return
	 */
	public static boolean setAgregMethod(HashMap<String, Double> list) {
		String methodName = "";
		for (String val : list.keySet()) {
			if (list.get(val) == 99.0) {
				methodName = val;
				break;
			}
		}
		if (methodName.endsWith("Moy")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * méthode de filou pour choper le nom d'une pondération
	 * 
	 * @param list
	 * @return
	 * @throws FileNotFoundException
	 */
	public static String getAHPName(HashMap<String, Double> list) throws FileNotFoundException {
		for (String nam : list.keySet()) {
			if (nam.length() > 5) {
				return nam;
			}
		}
		throw new FileNotFoundException("name of the ahp not found");
	}
}
