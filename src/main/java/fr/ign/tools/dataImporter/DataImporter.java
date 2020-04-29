package fr.ign.tools.dataImporter;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import org.locationtech.jts.io.ParseException;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.operation.TransformException;

/**
 * Classe permettant le traitement automatique des données pour une simulation avec MUP-City. Pour une explication complémentaire, merci de se référer à mon travail de thèse et en
 * particulier à l'annexe concernant les données. Le dossier "rootFileType" présent dans le dossier "src/main/ressource" du présent projet fournit une organisation basique des
 * dossiers devant comporter les données de base. Pour l'instant, le géocodage doit être réalisé à la main. Il faut donc lancer le code du main jusqu'à la méthode SortAmenity1part,
 * effectuer le géocodage manuel dans les fichiers tmp/sirene-loisir-geocoded.csv, puis lancer la deuxième partie du main depuis la méthode sortAmenity2part.
 * 
 * @author Maxime Colomb
 *
 */
public class DataImporter {

	private static String mainSRC = "2154";
	
	static boolean multipleDepartment = false;
	private static String nameFieldCodeCommunity = "INSEE";
	private static String nameFieldCodeGeoFla = "INSEE_COM";

	//Amenity SIRENE infos
	private static String nameFieldCodeSIRENE = "codeCommuneEtablissement";
	private  static String sireneSRC = "4326";
	private  static String sireneType = "activitePrincipaleEtablissement";

	//Amenity BPE infos
	private static String nameFieldCodeBPE = "DEPCOM";
	private  static String bpeSRC = "2154";
	private  static String bpeType = "TYPEQU";

	/**
	 * Classe principale. Renseigner la liste des départements et le rootFile
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		File rootFolder = new File("/home/ubuntu/donnees/rootFileType/");
		multipleDepartment = true;
		setUsualFolders(rootFolder);
		mainSetData();
	}

	private static void setUsualFolders(File rootFolder) throws MalformedURLException, NoSuchAuthorityCodeException, IOException, ParseException, FactoryException {
		
		File folderIn = new File(rootFolder, "dataIn");
		File folderOut = new File(rootFolder, "dataOut");
		Prepare.setRootFolder(rootFolder);
		Prepare.setFolderIn(folderIn);
		Prepare.setFolderOut(folderOut);
		new File(folderOut, "NU").mkdirs();
		Prepare.setTmpFolder(new File(rootFolder, "tmp"));
		new File(rootFolder, "tmp").mkdirs();
		Prepare.setBuildingFolder(new File(folderIn, "build"));
		Prepare.setTransportFolder(new File(folderIn, "transport"));
		Prepare.setAmenityFolder(new File(folderIn, "amenity"));
		Prepare.setAdminFile(new File(folderIn, "listeCommunities.csv"));
		Prepare.setVegeFolder(new File(folderIn, "vege"));
		Prepare.setNUFolder(new File(folderIn, "NU"));
		Prepare.setHydroFolder(new File(folderIn, "hydro"));
		Prepare.setEmpriseFile(new File(folderIn,"emprise.shp"));
		Prepare.setMultipleDepartment(multipleDepartment);
	}

	public static void mainSetData() throws NoSuchAuthorityCodeException, IOException, FactoryException, MismatchedDimensionException, ParseException, TransformException {

		// Bati
//		Prepare.prepareBuild();
//
//		// Road
//		Prepare.prepareRoad();
//
//		// Hydro
//		Prepare.prepareHydrography();
//
//		// Vegetation
//		Prepare.prepareVege();

		// Amenities

		// // IN CASE OF NO GEOCODED SIRENE POINTS
		// // sort the different amenities -first part (before the geocoding
		// sortAndGeocodeAmenities(rootFile, empriseFile, listDep, dbInfo);

		// IN CASE OF GEOCODED SIRENE POINTS (you can find french ones here : http://data.cquest.org/geo_sirene/v2019/last/dep/)
//		Prepare.sortAmenities();
		// Train
//		Prepare.prepareTrain();
		// Zones Non Urbanisables
		Prepare.makeFullZoneNU();
		//
		Prepare.makePhysicNU();
	}

	public static String getNameFieldCodeCommunity() {
		return nameFieldCodeCommunity;
	}

	public static void setNameFieldCodeCommunity(String nameFieldCodeCommunity) {
		DataImporter.nameFieldCodeCommunity = nameFieldCodeCommunity;
	}

	public static String getNameFieldCodeGeoFla() {
		return nameFieldCodeGeoFla;
	}

	public static void setNameFieldCodeGeoFla(String nameFieldCodeGeoFla) {
		DataImporter.nameFieldCodeGeoFla = nameFieldCodeGeoFla;
	}

	public static String getNameFieldCodeSIRENE() {
		return nameFieldCodeSIRENE;
	}

	public static void setNameFieldCodeSIRENE(String nameFieldCodeSIRENE) {
		DataImporter.nameFieldCodeSIRENE = nameFieldCodeSIRENE;
	}

	public static String getMainSRC() {
		return mainSRC;
	}

	public static void setMainSRC(String mainSRC) {
		DataImporter.mainSRC = mainSRC;
	}

	public static String getSireneSRC() {
		return sireneSRC;
	}

	public static void setSireneSRC(String sireneSRC) {
		DataImporter.sireneSRC = sireneSRC;
	}

	public static String getNameFieldCodeBPE() {
		return nameFieldCodeBPE;
	}

	public static void setNameFieldCodeBPE(String nameFieldCodeBPE) {
		DataImporter.nameFieldCodeBPE = nameFieldCodeBPE;
	}

	public static String getBpeSRC() {
		return bpeSRC;
	}

	public static void setBpeSRC(String bpeSRC) {
		DataImporter.bpeSRC = bpeSRC;
	}

	public static String getBpeType() {
		return bpeType;
	}

	public static void setBpeType(String bpeType) {
		DataImporter.bpeType = bpeType;
	}

	public static String getSireneType() {
		return sireneType;
	}

	public static void setSireneType(String sireneType) {
		DataImporter.sireneType = sireneType;
	}

}
