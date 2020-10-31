name := "git-explorer"

version := "0.1"

scalaVersion := "2.13.3"

mainClass in assembly := Some("com.github.comfreek.gitseek.cli.CLI")

libraryDependencies ++= Seq(
  // a CLI parsing library
  "com.github.scopt" %% "scopt" % "4.0.0-RC2",

  // a Git library
  "org.eclipse.jgit" % "org.eclipse.jgit" % "5.9.0.202009080501-r",

  // in order for the JAR created by `sbt package` to run standalone with pure Java and without Scala
  "org.scala-lang" % "scala-library-all" % "2.12.12"
)