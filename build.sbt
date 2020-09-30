lazy val Benchmark = config("bench") extend Test

ThisBuild / name := "and-challenges"
ThisBuild / version := "0.1"
ThisBuild / scalaVersion := "2.13.3"

lazy val root = (project in file("."))
  .enablePlugins(JmhPlugin, SuperSafeSbtPlugin)
  .settings(
    resolvers += "Artima Maven Repository" at "https://repo.artima.com/releases",
    libraryDependencies += "org.fusesource.jansi" % "jansi" % "1.18",
    libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2",
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.0",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.0" % Test,
    libraryDependencies += "org.scalatestplus" %% "scalacheck-1-14" % "3.2.2.0" % Test,
    libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.14.1" % Test,
    libraryDependencies += "org.scalamock" %% "scalamock" % "4.4.0" % Test,
    libraryDependencies += "com.storm-enroute" %% "scalameter" % "0.19" % Test,
//    libraryDependencies += "com.storm-enroute" %% "scalameter" % "0.19",
    Test / logBuffered := true,
    Test / parallelExecution := true
//    fork := true
//    Benchmark / logBuffered := false,
//    Benchmark / parallelExecution := false,
//    Benchmark / testOptions := Seq(Tests.Filter(isBenchmarkTest)),
//    Benchmark / fork := true,
//    Benchmark / outputStrategy := Some(StdoutOutput),
//    Benchmark / connectInput := true,
//    Benchmark / testFrameworks := Seq(new TestFramework("org.scalameter.ScalaMeterFramework")),
//    Benchmark / sourceDirectories := Seq(file("src/benchmark/scala"))
  )

//import sbt.Keys.parallelExecution
//
//lazy val Benchmark = config("bench") extend Test
//
//val project = Project(
//  "and-challenges-project",
//  file(".")
//) settings (
//  name := "and-challenges",
//  scalaVersion := "2.13.3",
//  scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-Xlint"),
//  publishArtifact := false,
//  libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2",
//  libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.0",
//  libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.0" % Test,
//  libraryDependencies += "org.scalatestplus" %% "scalacheck-1-14" % "3.2.2.0" % Test,
//  libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.14.1" % Test,
//  libraryDependencies += "org.scalamock" %% "scalamock" % "4.4.0" % Test,
//  libraryDependencies += "com.storm-enroute" %% "scalameter" % "0.19" % Benchmark,
//  resolvers ++= Seq(
//    "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
//    "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases",
//    "Artima Maven Repository" at "http://repo.artima.com/releases"
//  ),
//  logBuffered in Test := true,
//  parallelExecution in Test := true,
//  testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework"),
//) configs Benchmark settings (inConfig(Benchmark)(Defaults.testSettings): _*) settings (
//  parallelExecution in Benchmark := false,
//  logBuffered in Benchmark := false
//)
