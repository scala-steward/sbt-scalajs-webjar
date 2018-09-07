package de.lolhens.scalajs.webjar

import sbt.Defaults.TaskZero
import sbt.Keys._
import sbt._

import scala.language.implicitConversions

object WebjarPlugin extends AutoPlugin {

  object autoImport {
    lazy val Webjar = config("webjar") extend Compile
    //lazy val packageWebjar = taskKey[File]("Produces a WebJar.")

    implicit def webjarProject(project: Project): WebjarProject = WebjarProject.webjarProject(project)
  }

  import autoImport._

  override def projectConfigurations: Seq[Configuration] = Seq(
    Webjar
  )

  override lazy val projectSettings: Seq[Def.Setting[_]] =
    inConfig(Webjar) {
      Defaults.packageTaskSettings(packageBin, Def.task {
        def webjarMappings = (TaskZero / mappings).value

        def artifactName = name.value

        def artifactVersion = version.value

        webjarMappings.map {
          case (file, mapping) =>
            file -> s"META-INF/resources/webjars/$artifactName/$artifactVersion/$mapping"
        }
      }) ++ Seq(
        exportJars := true,

        exportedProductJars := {
          val data = packageBin.value
          val attributed = Attributed.blank(data)
            .put(artifact.key, (packageBin / artifact).value)
            .put(configuration.key, Webjar)

          Seq(attributed)
        },

        //dependencyClasspathAsJars := Seq.empty,
        //dependencyClasspath := Seq.empty,
        //externalDependencyClasspath := Seq.empty,

        //fullClasspathAsJars := exportedProductJars.value,
        //fullClasspath := fullClasspathAsJars.value,

        mappings := Seq.empty
      )
    }
}
