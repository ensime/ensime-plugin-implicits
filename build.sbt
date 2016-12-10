scalaVersion := "2.11.8"
organization := "org.ensime"
name := "ensime-plugin-implicits"

sonatypeGithub := ("ensime", name.value)
licenses := Seq(Apache2)

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-compiler" % scalaVersion.value,
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "org.ensime" %% "pcplod" % "1.1.0" % Test
)

javaOptions in Test ++= Seq(
  s"""-Dpcplod.settings=${(scalacOptions in Test).value.mkString(",")}""",
  s"""-Dpcplod.classpath=${(fullClasspath in Test).value.map(_.data).mkString(",")}"""
)

// too awkward to remove the deprecated warning for 2.10 / 2.11 diffs
scalacOptions -= "-Xfatal-warnings"
scalacOptions in Test ++= {
  val jar = (packageBin in Compile).value
  Seq(s"-Xplugin:${jar.getAbsolutePath}", s"-Jdummy=${jar.lastModified}") // ensures recompile
}
