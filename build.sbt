name := "and-challenges"

version := "0.1"

scalaVersion := "2.13.3"

resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"

libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.0"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.0" % Test
libraryDependencies += "org.scalatestplus" %% "scalacheck-1-14" % "3.2.2.0" % Test
libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.14.1" % Test
libraryDependencies += "org.scalamock" %% "scalamock" % "4.4.0" % Test

logBuffered in Test := true
parallelExecution in Test := true
