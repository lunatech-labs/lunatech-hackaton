name := """notEmptyTruck"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  "wabisabi" %% "wabisabi" % "2.0.10",
  "org.webjars" % "requirejs" % "2.1.14-1",
  "org.webjars" % "underscorejs" % "1.6.0-3",
  "org.webjars" % "jquery" % "1.11.1",
  "org.webjars" % "bootstrap" % "3.2.0" exclude("org.webjars", "jquery"),
  "org.webjars" % "angularjs" % "1.3.0-beta.2",
  "org.webjars" % "d3js" % "3.4.11",
  "org.webjars" % "jquery-throttle-debounce-plugin" % "1.1",
  "org.webjars" % "font-awesome" % "4.2.0",
  "com.github.nscala-time" %% "nscala-time" % "1.0.0",
  "org.webjars" % "bootstrap-datepicker" % "1.3.0-3",
  "org.webjars" % "d3js" % "3.4.11"
)
