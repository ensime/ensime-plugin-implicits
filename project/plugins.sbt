ivyLoggingLevel := UpdateLogging.Quiet
scalacOptions in Compile ++= Seq("-feature", "-deprecation")

addSbtPlugin("com.fommil" % "sbt-sensible" % "1.1.6")

addSbtPlugin("de.heikoseeberger" % "sbt-header" % "1.5.1")
