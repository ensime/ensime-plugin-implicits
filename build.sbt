scalaVersion := "2.12.1"
organization := "org.ensime"
name := "ensime-plugin-implicits"

sonatypeGithub := ("ensime", name.value)
licenses := Seq(Apache2)

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-compiler" % scalaVersion.value,
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "org.ensime" %% "pcplod" % "1.1.0" % Test,
  "com.chuusai" %% "shapeless" % "2.3.2" % Test
)

javaOptions in Test ++= {
  val jar = (packageBin in Compile).value
  val options = {
    (scalacOptions in Test).value :+
    s"-Xplugin:${jar.getAbsolutePath}" :+
    s"-Jdummy=${jar.lastModified}"
  }.mkString(",")
  val classpath = (fullClasspath in Test).value.map(_.data).mkString(",")
  Seq(
    s"""-Dpcplod.settings=$options""",
    s"""-Dpcplod.classpath=$classpath"""
  )
}

// too awkward to remove the deprecated warning for 2.10 / 2.11 diffs
scalacOptions -= "-Xfatal-warnings"
