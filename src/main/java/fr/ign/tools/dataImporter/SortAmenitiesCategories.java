package fr.ign.tools.dataImporter;

public class SortAmenitiesCategories {
	/**
	 * @deprecated that formalization is not present in the current open data siren
	 *             table. The real nomenclature can be seen here:
	 *             https://insee.fr/fr/statistiques/fichier/2406147/Nomenclatures_NAF_et_CPF_Edition_2019.pdf
	 *             TODO recreate an identical base with the actual codes ?
	 *             descriptions of the fields can be seen here:
	 *             https://static.data.gouv.fr/resources/base-sirene-des-entreprises-et-de-leurs-etablissements-siren-siret/20191126-150732/description-fichier-stocketablissement.pdf
	 *             Careful, some are under a awful other norm (info in the next
	 *             field i.e NAF1993 instead of NAFRev2. This looks like a boring an
	 *             long task => see if it's needed.
	 * @param amen    : the "" field from the Sirene file
	 * @param forMore : the "" type from the Sirene file
	 * @return
	 */
	public static String[] sortCatAmen(String amen, String forMore) {

		// return the shapefile it belongs to, the type and the level of visit frequency

		String[] classement = new String[3];
		switch (amen) {

		// CAS PARTICULIER ADMINISTRATION

		case "Administration publique générale":
			switch (forMore) {
			case "Enseignement primaire":
			case "Enseignement supérieur":
			case "Enseignement pré-primaire":
			case "Enseignement secondaire général":
			case "Enseignement secondaire technique ou professionnel":
				classement[0] = "service";
				classement[1] = "ecole";
				classement[2] = "1";
			}

			// SERVICES QUOTIDIENS

		case "Autres intermédiaires du commerce en denrées, boissons et tabac":
		case "Commerce de détail de journaux et papeterie en magasin spécialisé":
			classement[2] = " 1";
			classement[1] = "tabac";
			classement[0] = "service";
			break;
		case "Boulangerie et boulangerie-pâtisserie":
			classement[2] = " 1";
			classement[1] = "boulangerie";
			classement[0] = "service";
			break;
		case "Commerce d'alimentation générale":
		case "Commerce de détail alimentaire sur éventaires et marchés":
		case "Supérette":
			classement[2] = " 1";
			classement[1] = "superette";
			classement[0] = "service";
			break;
		case "Comm. détail poissons crustacés & mollusques (magasin spécialisé)":
		case "Comm. détail viandes & produits à base de viande (magas. spéc.)":
		case "Charcuterie":
			classement[2] = " 1";
			classement[1] = "boucherie";
			classement[0] = "service";
			break;

		// SERVICES HEBDOMADAIRES
		case "Activité des médecins généralistes":
			classement[2] = " 2";
			classement[1] = "medecin";
			classement[0] = "service";
			break;
//		case "Autres activités de poste et de courrier":
		case "Activ. poste dans le cadre d'une obligation de service universel":
			classement[2] = " 2";
			classement[1] = "poste";
			classement[0] = "service";
			break;
		case "Activités des centres de culture physique":
			classement[2] = " 2";
			classement[1] = "fitness";
			classement[0] = "service";
			break;
		case "Autres commerces de détail alimentaires en magasin spécialisé":
			classement[2] = " 2";
			classement[1] = "autre_alim";
			classement[0] = "service";
			break;
		case "Commerce de détail produits pharmaceutiques (magasin spécialisé)":
			classement[2] = " 2";
			classement[1] = "pharmacie";
			classement[0] = "service";
			break;
		case "Gestion des bibliothèques et des archives":
			classement[2] = " 2";
			classement[1] = "bibliotheque";
			classement[0] = "service";
			break;
		case "Coiffure":
			classement[2] = " 2";
			classement[1] = "coiffeur";
			classement[0] = "service";
			break;
		case "Débits de boissons":
			classement[2] = " 2";
			classement[1] = "bar";
			classement[0] = "service";
			break;
		case "Supermarché":
		case "Hypermarchés": // libel_naf5
			classement[2] = " 2";
			classement[1] = "supermarche";
			classement[0] = "service";
			break;
		case "Restauration de type rapide":
		case "Restauration collective sous contrat":
			classement[2] = " 2";
			classement[1] = "restaurant";
			classement[0] = "service";
			break;
		// case "Restauration traditionnelle":
		// classement[2] = " 2";
		// classement[1] = "restaurant";
		// classement[0] = "service";
		// break;

		case "F305":// Conservatoire
			classement[2] = " 2";
			classement[1] = "conservatoire";
			classement[0] = "service";
			break;

		// SERVICES MENSUELS
		case "Activités hospitalières":
			classement[2] = " 3";
			classement[1] = "hopital";
			classement[0] = "service";
			break;
		// case "Pratique dentaire":
		// classement[2] = " 3";
		// classement[1] = "specialiste";
		// classement[0] = "service";
		// break;
		case "Organisation de jeux de hasard et d'argent":
		case "Autres activités récréatives et de loisirs":
			classement[2] = " 3";
			classement[1] = "autre_equipement";
			classement[0] = "service";
			break;

		case "Projection de films cinématographiques":
		case "F303": // Cinéma
		case "F304": // Musée
		case "F302": // Théâtre
		case "Gestion de salles de spectacles":
		case "Gestion sites monuments historiques & attractions tourist. simil.":
			classement[2] = " 3";
			classement[1] = "equipement_culturel";
			classement[0] = "service";
			break;

		// LOISIRS QUOTIDIENS

		case "F111": // Plateaux et terrains de jeux extérieurs
			classement[2] = "1";
			classement[1] = "jeux";
			classement[0] = "loisir";
			break;

		// boulodrome

		// LOISIRS HEBDO (already from a codification (which?))

		case "F101": // Bassin de natation
		case "F118": // Sports nautiques
			classement[2] = "2";
			classement[1] = "piscine";
			classement[0] = "loisir";
			break;
		case "F102": // Boulodrome
			classement[2] = "2";
			classement[1] = "boulodrome";
			classement[0] = "loisir";
			break;
		case "F103": // Tennis
			classement[2] = "2";
			classement[1] = "tennis";
			classement[0] = "loisir";
			break;
		case "F104": // Équipement de cyclisme
			classement[2] = "2";
			classement[1] = "cyclisme";
			classement[0] = "loisir";
			break;
		case "F106": // Centre équestre
			classement[2] = "2";
			classement[1] = "equitation";
			classement[0] = "loisir";
			break;
		case "F107":// Athlétisme
			classement[2] = "2";
			classement[1] = "stade";
			classement[0] = "loisir";
			break;
		case "F109":// Parcours sportif/santé
			classement[2] = "2";
			classement[1] = "parcours";
			classement[0] = "loisir";
			break;
		case "Activités de clubs de sports":
			classement[2] = "2";
			classement[1] = "club-sport";
			classement[0] = "loisir";
			break;
		// case "F111":
		// switch (forMore){
		//
		// }
		// break;
		case "F112": // Salles spécialisées
			classement[2] = "2";
			classement[1] = "salle";
			classement[0] = "loisir";
			break;
		// case "F116":
		// classement[2] = "2";
		// classement[1] = "salle";
		// classement[0] = "loisir";
		// break;
		case "F114": // Salles de combat
			classement[2] = "2";
			classement[1] = "dojo";
			classement[0] = "loisir";
			break;
		case "F117": // Roller-Skate-Vélo bicross ou freestyle
			classement[2] = "2";
			classement[1] = "skatepark";
			classement[0] = "loisir";
			break;
		case "F121": // Salles multisports (gymnase)
			classement[2] = "2";
			classement[1] = "gymnase";
			classement[0] = "loisir";
			break;
		// Loisirs Mensuels

		case "F201": // Baignade aménagée
		case "F202": // Port de plaisance - Mouillage
		case "F113": // Terrain de grands jeux
			classement[2] = "2";
			classement[1] = "base-loisir";
			classement[0] = "loisir";
			break;

		// trains

		case "E103": // Gare avec desserte train à grande vitesse (TAGV)
			classement[2] = "";
			classement[1] = "LGV";
			classement[0] = "train";
			break;
		case "E106": // Gare sans desserte train à grande vitesse (TAGV)
			classement[2] = "";
			classement[1] = "normal";
			classement[0] = "train";
			break;

		}
		return classement;
	}

	/**
	 * The real nomenclature can be seen here:
	 * https://insee.fr/fr/statistiques/fichier/2406147/Nomenclatures_NAF_et_CPF_Edition_2019.pdf
	 * TODO recreate an identical base with the actual codes ? descriptions of the
	 * fields can be seen here:
	 * https://static.data.gouv.fr/resources/base-sirene-des-entreprises-et-de-leurs-etablissements-siren-siret/20191126-150732/description-fichier-stocketablissement.pdf
	 * Careful, some are under a awful other norm (info in the next field i.e
	 * NAF1993 instead of NAFRev2. This looks like a boring an long task => see if
	 * it's needed.
	 * 
	 * @param amen    : the "" field from the Sirene file
	 * @param forMore : the "" type from the Sirene file
	 * @return
	 */
	public static String[] sortCategoriesAmenenitiesNAFCPF(String amen) {

		// return the shapefile it belongs to, the type and the level of visit frequency

		String[] classement = new String[3];
		if (amen.startsWith("85")) {
			// CAS PARTICULIER ADMINISTRATION
			switch (amen) {
			case "85.2":
			case "85.3":
			case "85.4":
				classement[0] = "service";
				classement[1] = "ecole";
				classement[2] = "1";
				break;
			}
		} else if (amen.startsWith("47")) {
			boolean autre = false;
			switch (amen) {
			case "47.26":
			case "47.26Z":
			case "47.00.62":
			case "47.00.27":
				classement[2] = " 1";
				classement[1] = "tabac";
				classement[0] = "service";
				break;
			case "47.00.13":
			case "47.00.14":
			case "47.00.15":
			case "47.22Z":
			case "47.22":
			case "47.23Z":
			case "47.23":
				classement[2] = " 1";
				classement[1] = "boucherie";
				classement[0] = "service";
				break;
			case "47.11B":
			case "47.21":
			case "47.11C":
			case "47.21Z":
			case "47.00.11":
			case "47.00.12":
				classement[2] = " 1";
				classement[1] = "superette";
				classement[0] = "service";
				break;
			case "47.11D":
			case "47.11F":
				classement[2] = " 2";
				classement[1] = "supermarche";
				classement[0] = "service";
				break;
			case "47.00.16":
			case "47.24Z":
			case "47.24":
				classement[2] = " 1";
				classement[1] = "boulangerie";
				classement[0] = "service";
				break;
			default:
				autre = true;
			}
			if (autre & (amen.startsWith("47.00.2") || amen.startsWith("47.00.1") || amen.startsWith("47.29"))) {
				classement[2] = " 2";
				classement[1] = "autre_alim";
				classement[0] = "service";
			}
		} else if (amen.startsWith("10.71")) {
			classement[2] = " 1";
			classement[1] = "boulangerie";
			classement[0] = "service";
		} else if (amen.startsWith("53.1")) {
			classement[2] = " 2";
			classement[1] = "poste";
			classement[0] = "service";
		} else if (amen.startsWith("86.1")) {
			classement[2] = " 3";
			classement[1] = "hopital";
			classement[0] = "service";
		} else if (amen.startsWith("86.21")) {
			classement[2] = " 2";
			classement[1] = "medecin";
			classement[0] = "service";
		} else if (amen.startsWith("93.13")) {
			classement[2] = " 2";
			classement[1] = "fitness";
			classement[0] = "service";
		} else if (amen.startsWith("47.00.74") || amen.startsWith("47.73")) {
			classement[2] = " 2";
			classement[1] = "pharmacie";
			classement[0] = "service";

		} else if (amen.startsWith("56.3")) {
			classement[2] = " 2";
			classement[1] = "bar";
			classement[0] = "service";
		} else if (amen.startsWith("56.1")) {
			classement[2] = " 2";
			classement[1] = "restaurant";
			classement[0] = "service";
		} else if (amen.startsWith("91.01")) {
			classement[2] = " 2";
			classement[1] = "bibliotheque";
			classement[0] = "service";
		} else if (amen.startsWith("96.02")) {
			classement[2] = " 2";
			classement[1] = "coiffeur";
			classement[0] = "service";
		} else if (amen.startsWith("93.2")) {
			classement[2] = " 3";
			classement[1] = "autre_equipement";
			classement[0] = "service";
		} else if (amen.startsWith("91.03") || amen.startsWith("59.14") || amen.startsWith("90.04")
				|| amen.startsWith("91.02")) {
			classement[2] = " 3";
			classement[1] = "equipement_culturel";
			classement[0] = "service";

		} else if (amen.startsWith("92")) {
			classement[2] = " 3";
			classement[1] = "autre_equipement";
			classement[0] = "service";
		} else if (amen.startsWith("93.12")) {
			classement[2] = "2";
			classement[1] = "club-sport";
			classement[0] = "loisir";
		}
		// BPE cases
		switch (amen) {

		case "F305":// Conservatoire
			classement[2] = " 2";
			classement[1] = "conservatoire";
			classement[0] = "service";
			break;
		case "F302": // Théâtre
			classement[2] = " 3";
			classement[1] = "equipement_culturel";
			classement[0] = "service";
			break;
		case "F303": // Cinéma
			classement[2] = " 3";
			classement[1] = "equipement_culturel";
			classement[0] = "service";
			break;
		case "F304": // Musée
			classement[2] = " 3";
			classement[1] = "equipement_culturel";
			classement[0] = "service";
			break;

		// LOISIRS QUOTIDIENS

		case "F111": // Plateaux et terrains de jeux extérieurs
			classement[2] = "1";
			classement[1] = "jeux";
			classement[0] = "loisir";
			break;

		// boulodrome

		// LOISIRS HEBDO

		case "F101": // Bassin de natation
			classement[2] = "2";
			classement[1] = "piscine";
			classement[0] = "loisir";
			break;
		case "F102": // Boulodrome
			classement[2] = "2";
			classement[1] = "boulodrome";
			classement[0] = "loisir";
			break;
		case "F103": // Tennis
			classement[2] = "2";
			classement[1] = "tennis";
			classement[0] = "loisir";
			break;
		case "F104": // Équipement de cyclisme
			classement[2] = "2";
			classement[1] = "cyclisme";
			classement[0] = "loisir";
			break;
		case "F106": // Centre équestre
			classement[2] = "2";
			classement[1] = "equitation";
			classement[0] = "loisir";
			break;
		case "F107":// Athlétisme
			classement[2] = "2";
			classement[1] = "stade";
			classement[0] = "loisir";
			break;
		case "F109":// Parcours sportif/santé
			classement[2] = "2";
			classement[1] = "parcours";
			classement[0] = "loisir";
			break;
		case "F118": // Sports nautiques
			classement[2] = "2";
			classement[1] = "piscine";
			classement[0] = "loisir";
			break;
		// case "F111":
		// switch (forMore){
		//
		// }
		// break;
		case "F112": // Salles spécialisées
			classement[2] = "2";
			classement[1] = "salle";
			classement[0] = "loisir";
			break;
		// case "F116":
		// classement[2] = "2";
		// classement[1] = "salle";
		// classement[0] = "loisir";
		// break;
		case "F113": // Terrain de grands jeux
			classement[2] = "2";
			classement[1] = "base-loisir";
			classement[0] = "multi-sport";
			break;
		case "F114": // Salles de combat
			classement[2] = "2";
			classement[1] = "dojo";
			classement[0] = "loisir";
			break;
		case "F117": // Roller-Skate-Vélo bicross ou freestyle
			classement[2] = "2";
			classement[1] = "skatepark";
			classement[0] = "loisir";
			break;
		case "F121": // Salles multisports (gymnase)
			classement[2] = "2";
			classement[1] = "gymnase";
			classement[0] = "loisir";
			break;
		// Loisirs Mensuels

		case "F201": // Baignade aménagée
			classement[2] = "2";
			classement[1] = "base-loisir";
			classement[0] = "loisir";
			break;
		case "F202": // Port de plaisance - Mouillage
			classement[2] = "2";
			classement[1] = "base-loisir";
			classement[0] = "loisir";
			break;
		// trains

		case "E103": // Gare avec desserte train à grande vitesse (TAGV)
			classement[2] = "";
			classement[1] = "LGV";
			classement[0] = "train";
			break;
		case "E106": // Gare sans desserte train à grande vitesse (TAGV)
			classement[2] = "";
			classement[1] = "normal";
			classement[0] = "train";
			break;

		}
		return classement;
	}
}
