name := "mupcity-openmole"

version := "1.0"

scalaVersion := "2.12.6"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")
scalacOptions += "-target:jvm-1.8"

enablePlugins(SbtOsgi)

//osgiSettings     // import settings for OSGi plugin

//spiFlySettings   // import settings for spifly plugin

OsgiKeys.exportPackage := Seq("fr.ign.*;-split-package:=merge-first,fr.ign.task.*;-split-package:=merge-first,mupcityplugin.*;-split-package:=merge-first")

OsgiKeys.importPackage := Seq("*;resolution:=optional")

OsgiKeys.privatePackage := Seq("!scala.*,!java.*,META-INF.services.*,META-INF.*,*")

OsgiKeys.requireCapability := """osgi.ee;filter:="(&(osgi.ee=JavaSE)(version=1.8))""""

//scalariformSettings

excludeFilter in unmanagedSources := HiddenFileFilter || "*CompData.java" || "*MouvData.java" || "*MouvGrid.java" || "*TestStabilite.java" || "*TotalTests.java"

resolvers += "IDB" at "http://igetdb.sourceforge.net/maven2-repository/"

//resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"

resolvers += Resolver.mavenLocal

resolvers += "IGN snapshots" at "https://forge-cogit.ign.fr/nexus/content/repositories/snapshots"

resolvers += "IGN releases" at "https://forge-cogit.ign.fr/nexus/content/repositories/releases"

//resolvers += "ISC-PIF Public" at "http://maven.iscpif.fr/public/"

//resolvers += "ISC-PIF Snapshots" at "http://maven.iscpif.fr/ign-snapshots/"

//resolvers += "ISC-PIF Release" at "http://maven.iscpif.fr/ign-releases/"

resolvers += "ImageJ" at "http://maven.imagej.net/content/repositories/public"

//resolvers += "Central" at "http://repo1.maven.org/maven2/"

resolvers += "Boundless" at "http://repo.boundlessgeo.com/main"

resolvers += "osgeo" at "http://download.osgeo.org/webdav/geotools/"

resolvers += "geosolutions" at "http://maven.geo-solutions.it/"

resolvers += "Hibernate" at "http://www.hibernatespatial.org/repository"

//val openMOLEVersion = "5.0-SNAPSHOT"

//libraryDependencies += "org.openmole" %% "org-openmole-core-dsl" % openMOLEVersion

//libraryDependencies += "org.openmole" %% "org-openmole-plugin-task-scala" % openMOLEVersion

val mupcityVersion = "1.2.2"
val geotoolsVersion = "19.1"
val geotoolsGridVersion = "19.1"
val fracgisVersion = "0.6.3"

libraryDependencies += "org.thema" % "fracgis" % fracgisVersion excludeAll(
  ExclusionRule(organization = "org.geotools"),
  ExclusionRule(organization = "com.vividsolutions"),
  ExclusionRule(organization = "org.apache.batik")
)
libraryDependencies += "org.thema" % "mupcity" % mupcityVersion excludeAll(
  ExclusionRule(organization = "org.geotools"),
  ExclusionRule(organization = "com.vividsolutions"),
  ExclusionRule(organization = "org.apache.batik")
)
libraryDependencies += "org.geotools" % "gt-grid" % geotoolsGridVersion
libraryDependencies += "org.geotools" % "gt-coverage" % geotoolsGridVersion

//libraryDependencies += "org.geotools" % "gt-geotiff" % geotoolsVersion

/*
libraryDependencies += "it.geosolutions.jaiext.utilities" % "jt-utilities" % "1.0.8" excludeAll(
    ExclusionRule(organization = "javax.media", artifact = "jai*")
  )
*/

// val jaiextVersion = "1.0.11"

//libraryDependencies += "it.geosolutions.jaiext" % "jaiext" % jaiextVersion

//libraryDependencies += "com.github.jai-imageio" % "jai-imageio-core" % "1.3.1"

//libraryDependencies += "it.geosolutions.jaiext.crop" % "jt-crop" % jaiextVersion

OsgiKeys.additionalHeaders :=  Map(
			   "Specification-Title" -> "Spec Title",
			   "Specification-Version" -> "Spec Version",
			   "Specification-Vendor" -> "IGN",
			   "Implementation-Title" -> "Impl Title",
			   "Implementation-Version" -> "Impl Version",
			   "Implementation-Vendor" -> "IGN"
)
