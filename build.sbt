import com.typesafe.sbt.packager.docker.Cmd

scalaVersion := "2.13.1"

name := "sbt-native-packager-issue-1365"
version := "1.0"

enablePlugins(JavaAppPackaging)

// Uncomment this for a workaround, but do `git clean -xdff` in between.
// dockerGroupLayers in Docker := PartialFunction.empty

mappings in Docker += file(
  s"${baseDirectory.value}/src/main/resources/foobar.yaml"
) -> "/res/foobar.yaml"

dockerCommands += Cmd("COPY", "res", "/res")
