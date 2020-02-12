package fr.ign.tools.dataImporter;

import java.io.File;

public class DataImporter {

	static boolean multipleDepartment = false;

	/**
	 * Classe permettant le traitement automatique des données pour une simulation
	 * avec MUP-City. Pour une explication complémentaire, merci de se référer à mon
	 * travail de thèse et en particulier à l'annexe concernant les données. Le
	 * dossier "rootFileType" présent dans le dossier "src/main/ressource" du
	 * présent projet fournit une organisation basique des dossiers devant comporter
	 * les données de base. Pour l'instant, le géocodage doit être réalisé à la
	 * main. Il faut donc lancer le code du main jusqu'à la méthode
	 * SortAmenity1part, éfféctuer le géocodage manuel dans les fichiers
	 * tmp/sirene-loisir-geocoded.csv, puis lancer la deuxième partie du main depuis
	 * la méthode sortAmenity2part.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		File rootFolder = new File("/home/thema/donnees/rootFileType/");
		// emprise File
//		empriseFile = createEmpriseFile(rootFolder, new File(rootFolder, "dataIn/admin.csv"));

		Integer[] listDept = { 25, 70, 39 };

		if (listDept.length > 1) {
			multipleDepartment = true;
		}
		setUsualFolders(rootFolder);

		Prepare.sortAmenities();

//		String[] dbInfo = { "jdbc:postgresql://localhost:5432/sirene", "postgres", "postgres" };
//		
//		
//		
//		mainSetData(listDep, dbInfo);

	}

	private static void setUsualFolders(File rootFolder) {
		File folderIn = new File(rootFolder, "dataIn");
		File folderOut = new File(rootFolder, "dataOut");
		Prepare.setFolderIn(folderIn);
		Prepare.setFolderOut(folderOut);
		Prepare.setTmpFolder(new File(rootFolder, "tmp"));
		new File(rootFolder, "tmp").mkdir();
		Prepare.setBuildingFolder(new File(folderIn, "build"));
		Prepare.setRoadFolder(new File(folderIn, "road"));
		Prepare.setAmenityFolder(new File(folderIn, "amenity"));
		Prepare.setAdminFile(new File(folderIn, "listeCommunities.csv"));
		Prepare.setVegeFolder(new File(folderIn, "vege"));
		Prepare.setTrainFolder(new File(folderIn, "train"));
		Prepare.setNUFolder(new File(folderIn, "NU"));
		Prepare.setHydroFolder(new File(folderIn, "hydro"));
		Prepare.setEmpriseFile(new File(folderIn,"emprise.shp"));
		Prepare.setMultipleDepartment(multipleDepartment);
		
	}

	public static void mainSetData(Integer[] listDep, String[] dbInfo) throws Exception {

		// Bati
		Prepare.prepareBuildBDTopo12();

		// Road
		Prepare.prepareRoad();

		// Hydro
		Prepare.prepareHydrography();

		// Vegetation
		Prepare.prepareVege();

		// Amenities

		// // IN CASE OF NO GEOCODED SIRENE POINTS
		// // sort the different amenities -first part (before the geocoding
		// sortAndGeocodeAmenities(rootFile, empriseFile, listDep, dbInfo);

		// IN CASE OF GEOCODED SIRENE POINTS (you can find french ones here :
		// http://data.cquest.org/geo_sirene/v2019/last/dep/)
		Prepare.sortAmenities();
		// Train
		Prepare.prepareTrain();
		// Zones Non Urbanisables
		Prepare.makeFullZoneNU();
		//
		Prepare.makePhysicNU();

	}

}
