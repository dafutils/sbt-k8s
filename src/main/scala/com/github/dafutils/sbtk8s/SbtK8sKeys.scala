package com.github.dafutils.sbtk8s

import sbt._

trait SbtK8sKeys {
  lazy val k8sContext = settingKey[String]("The context that kubectl should use to deploy your manifests")
  lazy val k8sManifestsRootDirectory = settingKey[File]("The root directory that contains the k8s manifests file to deploy")
}
