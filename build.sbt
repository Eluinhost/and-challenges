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
    libraryDependencies += "com.googlecode.lanterna" % "lanterna" % "3.0.3",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.0" % Test,
    libraryDependencies += "org.scalatestplus" %% "scalacheck-1-14" % "3.2.2.0" % Test,
    libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.14.1" % Test,
    libraryDependencies += "org.scalamock" %% "scalamock" % "4.4.0" % Test,
    libraryDependencies += "com.storm-enroute" %% "scalameter" % "0.19" % Test,
    Test / logBuffered := true,
    Test / parallelExecution := true
  )
