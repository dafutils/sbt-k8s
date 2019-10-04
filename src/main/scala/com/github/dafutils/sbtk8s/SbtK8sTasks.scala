package com.github.dafutils.sbtk8s

import java.io.File

import sbt.taskKey

trait SbtK8sTasks {
  lazy val k8sCreateDockerImages = taskKey[Any]("Generate the docker images for all applications built from source")
  lazy val k8sCreateK8sManifests = taskKey[Seq[File]]("Generate the kubernetes manifests to deploy and return their location")
  lazy val k8sDeploy = taskKey[Unit]("Deploys the kubernetes manifests in the target k8s cluster")
  lazy val k8sTeardown = taskKey[Unit]("Remove all k8s objects created by running a deploy task previously")
  lazy val k8sRedeploy = taskKey[Unit]("Redeploy a previously deployed set of manifests")
}
