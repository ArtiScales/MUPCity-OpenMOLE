name := "mupcity-openmole-plugin"

version := "1.0"

scalaVersion := "2.11.8"

enablePlugins(SbtOsgi)

OsgiKeys.exportPackage := Seq("simplu3dopenmoleplugin.*")

OsgiKeys.importPackage := Seq("*;resolution:=optional")

OsgiKeys.privatePackage := Seq("!scala.*,!java.*,*")



resolvers += "IDB" at "http://igetdb.sourceforge.net/maven2-repository/"

//resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"

resolvers += Resolver.mavenLocal

//resolvers += "IGN snapshots" at "https://forge-cogit.ign.fr/nexus/content/repositories/snapshots/"

//resolvers += "IGN releases" at "https://forge-cogit.ign.fr/nexus/content/repositories/releases/"

//resolvers += "ISC-PIF Public" at "http://maven.iscpif.fr/public/"

//resolvers += "ISC-PIF Snapshots" at "http://maven.iscpif.fr/ign-snapshots/"

//resolvers += "ISC-PIF Release" at "http://maven.iscpif.fr/ign-releases/"

//resolvers += "ImageJ" at "http://maven.imagej.net/content/repositories/public"


//resolvers += "Boundless" at "http://repo.boundlessgeo.com/main"

//resolvers += "osgeo" at "http://download.osgeo.org/webdav/geotools/"

//resolvers += "geosolutions" at "http://maven.geo-solutions.it/"

//resolvers += "Hibernate" at "http://www.hibernatespatial.org/repository"

//val openMOLEVersion = "5.0-SNAPSHOT"

//libraryDependencies += "org.openmole" %% "org-openmole-core-dsl" % openMOLEVersion

//libraryDependencies += "org.openmole" %% "org-openmole-plugin-task-scala" % openMOLEVersion



libraryDependencies += "fr.ign" % "mupcity-openMole" % "0.0.1-SNAPSHOT" excludeAll(
    ExclusionRule(organization = "uk.ac.ed.ph.snuggletex"),
    ExclusionRule(organization = "vigna.dsi.unimi.it"),
    ExclusionRule(organization = "net.billylieurance.azuresearch"),
    ExclusionRule(organization = "net.sf.jafama"),
    ExclusionRule(organization = "net.sourceforge.jmatio"),
    ExclusionRule(organization = "jgrapht"),
    ExclusionRule(organization = "org.bethecoder"),
    ExclusionRule(organization = "com.aetrion.flickr"),
    ExclusionRule(organization = "com.caffeineowl.graphics"),
    ExclusionRule(organization = "de.bwaldvogel"),
    ExclusionRule(organization = "mnstarfire"),
    ExclusionRule(organization = "com.thoughtworks.xstream"),
    ExclusionRule(organization = "jfree"),
    ExclusionRule(organization = "javax.media"),
    ExclusionRule(organization = "org.slf4j")
)
