import com.typesafe.sbt.packager.docker.Cmd

scalaVersion := "2.13.1"

name := "sbt-native-packager-issue-1365"
version := "1.0"

enablePlugins(JavaAppPackaging)

mappings in Docker += file(
  s"${baseDirectory.value}/src/main/resources/foobar.yaml"
) -> "/res/foobar.yaml"

dockerCommands += Cmd("COPY", "res", "/res")
