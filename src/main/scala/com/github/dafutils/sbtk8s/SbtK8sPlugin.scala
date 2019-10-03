package com.github.dafutils.sbtk8s

import sbt._

object SbtK8sPlugin extends AutoPlugin {
  override val trigger: PluginTrigger = noTrigger
  override val requires: Plugins = plugins.JvmPlugin

  object autoImport extends SbtK8sKeys with SbtK8sTasks

  import autoImport._

  private def resolveManifests = Def.task {
    val manifestsRoot = k8sManifestsRootDirectory.value
    require(manifestsRoot.isDirectory, "The specified paths is not a directory")
    manifestsRoot.listFiles().toSeq
    
  }

  private def k8sDeployTask = Def.taskDyn {
    Def.task {
      val k8sManifests = resolveManifests.value
      import scala.sys.process._
      k8sManifests.map(manifest =>
        manifest -> (s"kubectl --context=${k8sContext.value} create -f ${manifest.getAbsolutePath}" !)
      )
    }
  }

  private def k8sUndeployTask = Def.taskDyn {
    Def.task {
      import scala.sys.process._
      val k8sManifests = resolveManifests.value
      k8sManifests.foreach(manifest =>
        manifest -> (s"kubectl --context=${k8sContext.value} delete -f ${manifest.getAbsolutePath}" !)
      )
    }
  }

  private def k8sReDeployTask =
    Def.sequential(k8sUndeployTask, k8sDeployTask)
  

  override lazy val projectSettings: Seq[Setting[_]] = Seq(
    k8sContext := "non-existant",
    k8sManifestsRootDirectory := file("non-existant"),
    k8sDeploy := k8sDeployTask.value,
    k8sRedeploy := k8sReDeployTask.value,
    k8sTeardown := k8sUndeployTask.value
  )
}
