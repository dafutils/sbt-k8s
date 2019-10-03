
lazy val root = (project in file("."))
  .settings(
    name := "sbt-k8s",
    organization := "com.github.dafutils",
    version := "0.7-SNAPSHOT",
    sbtPlugin := true,
//    scriptedLaunchOpts += ("-Dplugin.version=" + version.value),
//    scriptedLaunchOpts ++= sys.process.javaVmArguments.filter(
//      a => Seq("-Xmx", "-Xms", "-XX", "-Dsbt.log.noformat").exists(a.startsWith)
//    ),
//    scriptedBufferLog := false
  )
