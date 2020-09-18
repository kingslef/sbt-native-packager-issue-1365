# Reproduction for sbt-native-packager's [issue 1365](https://github.com/sbt/sbt-native-packager/issues/1365)

This repo contains minimal reproducible test case for sbt-native-packager's [issue 1365](https://github.com/sbt/sbt-native-packager/issues/1365). On certain platform(s), `sbt docker:publishLocal` fails.

It looks like the problem occurs when there is a mapping in `build.sbt`:
```
mappings in Docker += file(
  s"${baseDirectory.value}/src/main/resources/foobar.yaml"
) -> "/res/foobar.yaml"

dockerCommands += Cmd("COPY", "res", "/res")
```

There is a workaround when layered docker builds are disabled by adding the following to `build.sbt`:
```
dockerGroupLayers in Docker := PartialFunction.empty
```

## How to reproduce the error

- Run `sbt docker:publishLocal`:

  ```
  $ sbt docker:publishLocal
  ...
  [info] Sending build context to Docker daemon  5.751MB
  [info] Step 1/23 : FROM openjdk:8 as stage0
  [info]  ---> 51d6b33ebe8a
  [info] Step 2/23 : LABEL snp-multi-stage="intermediate"
  [info]  ---> Running in f26461db4344
  [info] Removing intermediate container f26461db4344
  [info]  ---> 5d782a974aad
  [info] Step 3/23 : LABEL snp-multi-stage-id="b4546d31-6295-4076-88b6-e2d3259c828a"
  [info]  ---> Running in 587765eeedde
  [info] Removing intermediate container 587765eeedde
  [info]  ---> e3e4ee2165c9
  [info] Step 4/23 : WORKDIR /opt/docker
  [info]  ---> Running in ac11ccd0702f
  [info] Removing intermediate container ac11ccd0702f
  [info]  ---> e27c707050ab
  [info] Step 5/23 : COPY opt /opt
  [error] COPY failed: stat /var/lib/docker/tmp/docker-builder114860174/opt: no such file or directory
  [info] Removing intermediate image(s) (labeled "snp-multi-stage-id=b4546d31-6295-4076-88b6-e2d3259c828a")
  [info] Deleted Images:
  [info] deleted: sha256:e27c707050ab58cdd238ad8585561499cb6770b0319faeda9cbf887e0edeba53
  [info] deleted: sha256:0d29f41bbb777f0f778d675cd064f5f4c1cbb29675ccea6a090525721f15b53a
  [info] deleted: sha256:e3e4ee2165c9367ac62a68a49b795cb2427fee7a490e2dec4694ba6b4bc123e1
  [info] deleted: sha256:5d782a974aad59d6c19ee59fab72656bd14b6e4de6a76eced14daadeeea46a0e
  [info] Total reclaimed space: 0B
  [error] java.lang.RuntimeException: Nonzero exit value: 1
  [error] 	at com.typesafe.sbt.packager.docker.DockerPlugin$.publishLocalDocker(DockerPlugin.scala:631)
  [error] 	at com.typesafe.sbt.packager.docker.DockerPlugin$.$anonfun$projectSettings$49(DockerPlugin.scala:247)
  [error] 	at com.typesafe.sbt.packager.docker.DockerPlugin$.$anonfun$projectSettings$49$adapted(DockerPlugin.scala:239)
  [error] 	at scala.Function1.$anonfun$compose$1(Function1.scala:49)
  [error] 	at sbt.internal.util.$tilde$greater.$anonfun$$u2219$1(TypeFunctions.scala:62)
  [error] 	at sbt.std.Transform$$anon$4.work(Transform.scala:67)
  [error] 	at sbt.Execute.$anonfun$submit$2(Execute.scala:280)
  [error] 	at sbt.internal.util.ErrorHandling$.wideConvert(ErrorHandling.scala:19)
  [error] 	at sbt.Execute.work(Execute.scala:289)
  [error] 	at sbt.Execute.$anonfun$submit$1(Execute.scala:280)
  [error] 	at sbt.ConcurrentRestrictions$$anon$4.$anonfun$submitValid$1(ConcurrentRestrictions.scala:178)
  [error] 	at sbt.CompletionService$$anon$2.call(CompletionService.scala:37)
  [error] 	at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:264)
  [error] 	at java.base/java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:515)
  [error] 	at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:264)
  [error] 	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1130)
  [error] 	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:630)
  [error] 	at java.base/java.lang.Thread.run(Thread.java:832)
  [error] (Docker / publishLocal) Nonzero exit value: 1
  [error] Total time: 5 s, completed 18 Sep 2020, 18.25.45
  ```
