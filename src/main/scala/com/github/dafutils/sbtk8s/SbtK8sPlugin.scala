package com.github.dafutils.sbtk8s

import sbt._

object SbtK8sPlugin extends AutoPlugin {
  override val trigger: PluginTrigger = noTrigger
  override val requires: Plugins = plugins.JvmPlugin

  object autoImport extends SbtK8sKeys with SbtK8sTasks

  import autoImport._

  private def resolveManifests = Def.task {
    val manifestsRoot = k8sManifestsRootDirectory.value
    require(manifestsRoot.isDirectory, s"The specified manifest root path [${manifestsRoot.getAbsolutePath}] is not a directory")
    manifestsRoot.listFiles().toSeq
    
  }

  private def k8sDeployTask = Def.taskDyn {
    Def.task {
      val logger = sbt.Keys.streams.value.log
      val k8sManifests = resolveManifests.value
      import scala.sys.process._
      val creationResults = k8sManifests.map(manifest =>
        manifest -> (s"kubectl --context=${k8sContext.value} create -f ${manifest.getAbsolutePath}" !)
      )
      
      creationResults
        .filter{case (_, exitCode) => exitCode != 0}
        .map(_._1)
        .foreach(file => logger.log(Level.Error, s"Error deploying manifest in ${file.getAbsolutePath}"))
    }
  }

  private def k8sUndeployTask = Def.taskDyn {
    Def.task {
      val logger = sbt.Keys.streams.value.log
      import scala.sys.process._
      val k8sManifests = resolveManifests.value
      val deletionResults = k8sManifests.map(manifest =>
        manifest -> (s"kubectl --context=${k8sContext.value} delete -f ${manifest.getAbsolutePath}" !)
      )
      deletionResults
        .filter{case (_, exitCode) => exitCode != 0}
        .map(_._1)
        .foreach(file => logger.log(Level.Error, s"Error removing objects from  manifest in ${file.getAbsolutePath}"))
    }
  }

  private def k8sReDeployTask =
    Def.sequential(k8sUndeployTask, k8sDeployTask)
  

  override lazy val projectSettings: Seq[Setting[_]] = Seq(
    k8sContext := "non-existant",
    k8sManifestsRootDirectory := file("non-existant"),
    k8sDeploy := Def.sequential(k8sCreateDockerImages, k8sDeployTask).value,
    k8sRedeploy := Def.sequential(k8sCreateDockerImages, k8sReDeployTask).value,
    k8sTeardown := k8sUndeployTask.value
  )
}
