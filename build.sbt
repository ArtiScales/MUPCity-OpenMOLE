name := "mupcity-openmole"

version := "1.0"

scalaVersion := "2.12.6"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")
scalacOptions += "-target:jvm-1.8"

enablePlugins(SbtOsgi)

//osgiSettings     // import settings for OSGi plugin

//spiFlySettings   // import settings for spifly plugin

OsgiKeys.exportPackage := Seq("fr.ign,fr.ign.task,mupcityplugin")

//OsgiKeys.exportPackage := Seq(
//  "fr.ign.*;-split-package:=merge-first",
//  "fr.ign.task.*;-split-package:=merge-first",
//  "mupcityplugin.*;-split-package:=merge-first")
//"sun.awt","com.sun.imageio.spi","javax.imageio.spi"

//OsgiKeys.exportPackage := Seq("mupcityplugin;-split-package:=merge-first")

OsgiKeys.importPackage := Seq("*;resolution:=optional")//,com.sun.media.imageioimpl.plugins.tiff;resolution:=optional

OsgiKeys.privatePackage := Seq("""
|!scala.*,!java.*,META-INF.*;-split-package:=merge-first,
|*;-split-package:=merge-first
|""".stripMargin)

//!org.geotools.*,!com.sun.media.*,!javax.media.*,!it.geosolutions.*,!scala.*,!java.*,
//!META-INF.*.org.geotools.*,META-INF.*;-split-package:=merge-first,
//!META-INF.services.org.geotools.*,!META-INF.services.javax.media.jai.*,!META-INF.services.javax.imageio.spi.*,META-INF.services.*;-split-package:=merge-first,

//OsgiKeys.privatePackage := Seq(
//  "!scala.*",
//  "!java.*",
//  "META-INF.services.*;-split-package:=merge-first",
//  "META-INF.*;-split-package:=merge-first",
//  "*;-split-package:=merge-first")

OsgiKeys.requireCapability := """osgi.ee; osgi.ee="JavaSE";version:List="1.8,1.9"""""

//OsgiKeys.requireBundle := Seq(
//  "org.geotools.api;bundle-version=19.1",
//  "org.geotools.main;bundle-version=19.1",
//  "org.geotools.shapefile;bundle-version=19.1",
//  "org.geotools.geotiff;bundle-version=19.1",
//  "org.geotools.metadata;bundle-version=19.1",
//  "org.geotools.referencing;bundle-version=19.1",
//  "org.geotools.epsg-hsql;bundle-version=19.1",
//  "org.geotools.image;bundle-version=19.1"
//)

//OsgiKeys.bundleRequiredExecutionEnvironment := Seq("JavaSE-1.8")

//scalariformSettings

excludeFilter in unmanagedSources := HiddenFileFilter || "*CompData.java" || "*MouvData.java" || "*MouvGrid.java" || "*TestStabilite.java" || "*TotalTests.java"

//resolvers += "IDB" at "http://igetdb.sourceforge.net/maven2-repository/"

//resolvers += "Local Maven Repository" at "file:///home/mcolomb/.m2/repository"

resolvers += Resolver.mavenLocal

//resolvers += "IGN snapshots" at "https://forge-cogit.ign.fr/nexus/content/repositories/snapshots"

//resolvers += "IGN releases" at "https://forge-cogit.ign.fr/nexus/content/repositories/releases"

//resolvers += "ISC-PIF Public" at "http://maven.iscpif.fr/public/"

//resolvers += "ISC-PIF Snapshots" at "http://maven.iscpif.fr/ign-snapshots/"

//resolvers += "ISC-PIF Release" at "http://maven.iscpif.fr/ign-releases/"

//resolvers += "ImageJ" at "http://maven.imagej.net/content/repositories/public"

resolvers += "Central" at "http://repo1.maven.org/maven2/"

//resolvers += "Boundless" at "http://repo.boundlessgeo.com/main"

resolvers += "osgeo" at "http://download.osgeo.org/webdav/geotools/"

//resolvers += "geosolutions" at "http://maven.geo-solutions.it/"

//resolvers += "Hibernate" at "http://www.hibernatespatial.org/repository"

//val openMOLEVersion = "5.0-SNAPSHOT"

//libraryDependencies += "org.openmole" %% "org-openmole-core-dsl" % openMOLEVersion

//libraryDependencies += "org.openmole" %% "org-openmole-plugin-task-scala" % openMOLEVersion

val mupcityVersion = "1.2.3"
//val geotoolsVersion = "19.1"
val geotoolsGridVersion = "18.4"
val fracgisVersion = "0.7.1"

libraryDependencies += "org.geotools" % "gt-grid" % geotoolsGridVersion
libraryDependencies += "org.geotools" % "gt-coverage" % geotoolsGridVersion
libraryDependencies += "org.geotools" % "gt-geotiff" % geotoolsGridVersion
libraryDependencies += "org.geotools" % "gt-image" % geotoolsGridVersion
//
libraryDependencies += "org.geotools" % "gt-referencing" % geotoolsGridVersion
libraryDependencies += "org.geotools" % "gt-epsg-hsql" % geotoolsGridVersion
//libraryDependencies += "org.geotools" % "gt-epsg-extension" % geotoolsGridVersion
libraryDependencies += "org.geotools" % "gt-shapefile" % geotoolsGridVersion
libraryDependencies += "org.geotools" % "gt-graph" % geotoolsGridVersion
//
libraryDependencies += "org.geotools" % "gt-metadata" % geotoolsGridVersion
libraryDependencies += "org.geotools" % "gt-opengis" % geotoolsGridVersion
libraryDependencies += "org.geotools" % "gt-main" % geotoolsGridVersion
libraryDependencies += "org.geotools" % "gt-api" % geotoolsGridVersion

libraryDependencies += "org.thema" % "fracgis" % fracgisVersion excludeAll(
    ExclusionRule(organization = "org.geotools"),
  )
//excludeAll(
//  ExclusionRule(organization = "org.geotools"),
//  ExclusionRule(organization = "com.vividsolutions  ")
//  )

//excludeAll(
//  ExclusionRule(organization = "org.geotools"),
//  ExclusionRule(organization = "org.jdom"),
//  ExclusionRule(organization = "it.geosolutions.*"),
//  ExclusionRule(organization = "com.vividsolutions"),
//  ExclusionRule(organization = "org.apache.batik")
//)
libraryDependencies += "org.thema" % "mupcity" % mupcityVersion excludeAll(
  ExclusionRule(organization = "org.geotools")
  )
//excludeAll(
//  ExclusionRule(organization = "org.geotools"),
//  ExclusionRule(organization = "com.vividsolutions")
//  )

libraryDependencies += "org.jdom" % "jdom" % "1.1.3"

libraryDependencies += "xerces" % "xercesImpl" % "2.12.0"

//libraryDependencies += "it.geosolutions.imageio-ext" % "imageio-ext-tiff" % "1.1.20"

//libraryDependencies += "com.github.jai-imageio" % "jai-imageio-core" % "1.3.1"

//libraryDependencies += "com.github.jai-imageio" % "jai-imageio-core" % "1.4.0"

//excludeDependencies ++= Seq(
//  ExclusionRule("javax.media", "jai_imageio")
//)
excludeDependencies ++= Seq(
  ExclusionRule("com.vividsolutions", "jts")
//  ExclusionRule("javax.media","jai_imageio")
)

//excludeAll(
//  ExclusionRule(organization = "org.geotools"),
//  ExclusionRule(organization = "org.jdom"),
//  ExclusionRule(organization = "it.geosolutions.*"),
//  ExclusionRule(organization = "com.vividsolutions"),
//  ExclusionRule(organization = "org.apache.batik")
//)

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

//OsgiKeys.embeddedJars := (Keys.externalDependencyClasspath in Compile).value map (_.data) filter (_.getName contains "geotools") map (d=>{
//  println("dep = " + d)
//  d
//})

//enablePlugins(AssemblyPlugin)
//
//mergeStrategy in assembly := MergeStrategy.concat

//def myFilter(name: String) =
//  ((name contains "gt-") && !(name contains "opengis")) ||
//  (name contains "imageio") ||
//  (name contains "jai") ||
//  (name contains "jt-")
//
//OsgiKeys.embeddedJars := (Keys.externalDependencyClasspath in Compile).value map (_.data) filter (f=>myFilter(f.getName)) map (_.asFile)

//excludeFilter in Keys.externalDependencyClasspath := ("*gt-*" && -"*opengis") || "*imageio*" || "*jai*" || "*jt-*"
