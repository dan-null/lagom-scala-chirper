organization in ThisBuild := "sample.scala.chirper"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.11.8"

// Added: seems new or required
val macwire = "com.softwaremill.macwire" %% "macros" % "2.2.5" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1" % Test

lazy val friendApi = project("friend-api")
  .settings(
    version := "1.0-SNAPSHOT",
    libraryDependencies += lagomScaladslApi
  )

lazy val friendImpl = project("friend-impl")
  .enablePlugins(LagomScala)
  .settings(
    version := "1.0-SNAPSHOT",
    logLevel := Level.Debug,
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      macwire, // Added
      scalaTest, // Added
      lagomScaladslTestKit
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(friendApi)

lazy val chirpApi = project("chirp-api")
  .settings(
    version := "1.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      lagomScaladslApi
      //lagomScaladslJackson
    )
  )

lazy val chirpImpl = project("chirp-impl")
  .enablePlugins(LagomScala)
  .settings(
    version := "1.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslPubSub,
      macwire, // Added
      scalaTest, // Added
      lagomScaladslTestKit
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(chirpApi)

//lazy val activityStreamApi = project("activity-stream-api")
//  .settings(
//    version := "1.0-SNAPSHOT",
//    libraryDependencies += lagomScaladslApi
//  )
//  .dependsOn(chirpApi)
//
//lazy val activityStreamImpl = project("activity-stream-impl")
//  .enablePlugins(LagomScala)
//  .settings(
//    version := "1.0-SNAPSHOT",
//    libraryDependencies += lagomScaladslTestKit
//  )
//  .dependsOn(activityStreamApi, chirpApi, friendApi)
//
// lazy val frontEnd = project("front-end")
//   .enablePlugins(PlayScala, LagomPlay)
//   .settings(
//     version := "1.0-SNAPSHOT",
//     routesGenerator := InjectedRoutesGenerator,
//     libraryDependencies ++= Seq(
//       "org.webjars" % "react" % "0.14.8",
//       "org.webjars" % "react-router" % "1.0.3",
//       "org.webjars" % "jquery" % "2.2.4",
//       "org.webjars" % "foundation" % "5.5.2"
//     ),
//     ReactJsKeys.sourceMapInline := true
//   )


// lazy val loadTestApi = project("load-test-api")
//   .settings(
//     version := "1.0-SNAPSHOT",
//     libraryDependencies += lagomScaladslApi
//   )

// lazy val loadTestImpl = project("load-test-impl")
//   .enablePlugins(LagomScala)
//   .settings(version := "1.0-SNAPSHOT")
//   .dependsOn(loadTestApi, friendApi, activityStreamApi, chirpApi)

def project(id: String) = Project(id, base = file(id))
  .settings(javacOptions in compile ++= Seq("-encoding", "UTF-8", "-source", "1.8", "-target", "1.8", "-Xlint:unchecked", "-Xlint:deprecation"))
  .settings(jacksonParameterNamesJavacSettings: _*) // applying it to every project even if not strictly needed.


// See https://github.com/FasterXML/jackson-module-parameter-names
lazy val jacksonParameterNamesJavacSettings = Seq(
  javacOptions in compile += "-parameters"
)

// do not delete database files on start
lagomCassandraCleanOnStart in ThisBuild := false

// Kafka can be disabled until we need it
lagomKafkaEnabled in ThisBuild := false

licenses in ThisBuild := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))

