package fr.ign.tools.dataImporter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class ImportAmenitiesFromElsewhere {
	
	/**
	 * trie les aménités non géocodées - de SIRENE contenue dans une base de données locale - et de BPE contenue dans le fichier dataIn/sireneBPE/BPE-tot.csv
	 * 
	 * @param rootFile
	 *            : dossier principal
	 * @param empriseFile
	 *            : shp de l'emprise générale (auto-généré)
	 * @param nbDep
	 *            : liste des départements à prendre en compte
	 * @param dbInfo
	 *            : Tableau de String contenant les infos de connexion à la base de donnée contenant Sirene rangé dans l'ordre suivant : [0] Url de la bdd [1] User de la bdd [2] pw
	 *            du user
	 * @throws Exception
	 */
//	public static void sortAndGeocodeAmenities(File rootFile, File empriseFile, Integer[] nbDep, String[] dbInfo) throws Exception {
//		sortAndGeocodeSirene(rootFile, empriseFile,  nbDep, dbInfo);
//		sortBPE(rootFile, empriseFile);
//		mergeAmenities(rootFile, empriseFile);
//	}
//	
//		public static void sortAndGeocodeSirene(File rootFile, File empriseFile, Integer[] nbDep, String[] dbInfo) throws Exception {
//		
//		
//		File pointSireneIn = extractSireneFromDB(new File(rootFile, "tmp"), nbDep, dbInfo[0], dbInfo[1], dbInfo[2]);
//
//		// liste tirée du GÉOFLA et jointuré avec la poste pour avoir les codes postaux
//		File listVille = new File(rootFile, "dataIn/admin.csv");
//		File csvServicesSirene = new File(rootFile, "tmp/siren-Services.csv");
//		File csvLoisirsSirene = new File(rootFile, "tmp/siren-Loisirs.csv");
//
//		if (csvLoisirsSirene.exists()) {
//			Files.delete(csvLoisirsSirene.toPath());
//		}
//		if (csvServicesSirene.exists()) {
//			Files.delete(csvServicesSirene.toPath());
//		}
//
//		// for the sirene file
//		CSVReader csvSirenePruned = new CSVReader(new FileReader(preselecGeocode(pointSireneIn, listVille)));
//		CSVWriter csvServiceW = new CSVWriter(new FileWriter(csvServicesSirene, true));
//		CSVWriter csvLoisirW = new CSVWriter(new FileWriter(csvLoisirsSirene, true));
//		String[] firstLine = csvSirenePruned.readNext();
//		String[] newFirstLine = new String[firstLine.length + 2];
//		for (int k = 0; k < firstLine.length; k = k + 1) {
//			newFirstLine[k] = firstLine[k];
//		}
//		newFirstLine[firstLine.length] = "TYPE";
//		newFirstLine[firstLine.length + 1] = "LEVEL";
//		csvLoisirW.writeNext(newFirstLine);
//		csvServiceW.writeNext(newFirstLine);
//
//		for (String[] row : csvSirenePruned.readAll()) {
//			String[] result = new String[102];
//			String[] resultOut = sortCatAmen(row[43], row[73]);
//			if (!(resultOut[0] == null)) {
//				for (int i = 0; i < 100; i = i + 1) {
//					result[i] = row[i];
//				}
//				result[100] = resultOut[1];
//				result[101] = resultOut[2];
//				switch (resultOut[0]) {
//				case "service":
//					csvServiceW.writeNext(result);
//					break;
//				case "loisir":
//					csvLoisirW.writeNext(result);
//					break;
//				}
//			}
//		}
//
//		csvSirenePruned.close();
//		csvServiceW.close();
//		csvLoisirW.close();
//
//		}
//	
//	
//	
//	public static String geocodeAPIAdresse(String[] line) throws IOException {
//		// look here : https://geo.api.gouv.fr/adresse
//		// look also here : https://stackabuse.com/how-to-send-http-requests-in-java/
//		URL url = new URL("https://api-adresse.data.gouv.fr/search/?q="+""+""+"");
//		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//		connection.setRequestMethod("GET");
//		return null;
//	}
//		
//		
//		public static String geocodeBan(String targetURL, String urlParameters) throws IOException {
//			// TODO faire que le géocodage soit automatique (la BAN ne réponds pas - surement la faute du proxy ?)
//			HttpURLConnection connection = null;
//			try {
//				// Create connection
//				URL url = new URL(targetURL);
//				connection = (HttpURLConnection) url.openConnection();
//				connection.setRequestMethod("POST");
//				connection.setRequestProperty("Content-Type", "api");
//				connection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));
//				// connection.setRequestProperty("Content-Language", "fr-FR");
//				connection.setUseCaches(false);
//				connection.setDoOutput(true);
	//
//				// Send request
//				DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
//				wr.writeBytes(urlParameters);
//				wr.close();
//				// Get Response
//				InputStream is = connection.getInputStream();
//				BufferedReader rd = new BufferedReader(new InputStreamReader(is));
//				System.out.println(rd);
//				StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
//				String line;
//				while ((line = rd.readLine()) != null) {
//					response.append(line);
//					response.append('\r');
//				}
//				rd.close();
//				return response.toString();
//			} finally {
//				if (connection != null) {
//					connection.disconnect();
//				}
//			}
	//
//		}
//	/**
//	 * Geolocalise (merge) and sort sirene entries
//	 * TODO fix it 
//	 * 
//	 * @param rootFile
//	 * @param empriseFile
//	 * @param nbDep
//	 * @throws Exception
//	 */
//		public static void sortSirene() throws Exception {
//			File sireneEntries = new File(amenityFolder, "sirene.csv");
//		// if multiple points
//		if (multipleDepartment) {
//			sireneEntries = new File(tmpFolder, "sirene.csv");
//			Csv.mergeCSVFiles(amenityFolder, sireneEntries);
//		}
//
//		// Sirene points
//		DefaultFeatureCollection sireneServices = new DefaultFeatureCollection();
//		DefaultFeatureCollection sireneLoisirs = new DefaultFeatureCollection();
//
//		SimpleFeatureTypeBuilder PointSfTypeBuilder = new SimpleFeatureTypeBuilder();
//
//		PointSfTypeBuilder.setCRS(CRS.decode("EPSG:2154"));
//		PointSfTypeBuilder.add("the_geom", Point.class);
//		PointSfTypeBuilder.setDefaultGeometry("the_geom");
//		PointSfTypeBuilder.add("TYPE", String.class);
//		PointSfTypeBuilder.add("LEVEL", Integer.class);
//
//		Object[] attr =new Object[2];
//
//		SimpleFeatureType pointFeatureType = PointSfTypeBuilder.buildFeatureType();
//		SimpleFeatureBuilder pointSfBuilder = new SimpleFeatureBuilder(pointFeatureType);
//
//		int i = 0;
//
//
//				String[] resultOut = sortCatAmen((String) pt.getAttribute("libel_naf5"), (String) pt.getAttribute("libel_naf5"));
//				if (!(resultOut[0] == null)) {
//					pointSfBuilder.add((Geometry) pt.getDefaultGeometry());
//
//					attr[0] = resultOut[1];
//					attr[1] = resultOut[2];
//					SimpleFeature feature = pointSfBuilder.buildFeature(String.valueOf(i), attr);
//					switch (resultOut[0]) {
//					case "service":
//						sireneServices.add(feature);
//						break;
//					case "loisir":
//						sireneLoisirs.add(feature);
//						break;
//					}
//					i++;
//				}
//			}
//
//
//		Vectors.exportSFC(Vectors.cropSFC(sireneServices.collection(), empriseFile), new File(rootFolder, "tmp/siren-Services.shp"));
//		Vectors.exportSFC(Vectors.cropSFC(sireneLoisirs.collection(), empriseFile), new File(rootFolder, "tmp/siren-Loisirs.shp"));
//		System.out.println("c'est fait pour sirene");
//		SDSirene.dispose();
//	}
	public static File extractSireneFromDB(File tempFile, Integer[] depList, String DB_URL, String USER, String PASS) throws SQLException, IOException {
		File sireneFile = new File(tempFile, "sirene-dep-test.csv");
		Connection conn = null;
		boolean firstLine = true;
		boolean append = false;
		FileWriter writer = new FileWriter(sireneFile, append);
		append = true;
		try {
			Class.forName("org.postgresql.Driver"); // leve une exception si le pilote Postgresql n’est pas installe
			conn = DriverManager.getConnection(DB_URL, USER, PASS); // ouvre une connexion vers la base de donnees data
			Statement state = conn.createStatement();
			// L’objet ResultSet contient le resultat de la requete SQL
			for (int dep : depList) {
				String req = "SELECT * FROM sirn WHERE depet='" + dep + "'";
				ResultSet result = state.executeQuery(req); // execution d’une requete SQL
				ResultSetMetaData resultMeta = result.getMetaData();
				int colCount = resultMeta.getColumnCount();
				if (firstLine) {
					for (int i = 1; i <= colCount; i++) {
						writer.append(resultMeta.getColumnName(i));
						writer.append(";");
					}
					writer.append("\n");
				}
				while (result.next()) {
					for (int i = 1; i <= colCount; i++) {
						writer.append(result.getString(i));
						writer.append(";");
					}
					writer.append("\n");
				}
				firstLine = false;
				result.close();
			}
			state.close();
		} catch (Exception e) {
			e.printStackTrace(); // pour gerer les erreurs (pas de pilote, base inexistante, etc.)
		} finally {
			if (conn != null) {
				conn.close(); // toujours fermer les differentes ressources quand il n’y en as plus besoin
			}
		}
		writer.close();
		return sireneFile;
	}

}
