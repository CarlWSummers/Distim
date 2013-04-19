name := "DistEg"

version := "1.0"

scalaVersion := "2.10.1"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += Classpaths.typesafeResolver

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.1.2"

libraryDependencies += "org.scala-lang" % "scala-compiler" % "2.10.1"

libraryDependencies += "org.scala-lang" % "scala-swing" % "2.10.1"

libraryDependencies += "org.simplex3d" %% "simplex3d-engine-default" %"0.3-SNAPSHOT"

scalacOptions += "-deprecation"

scalacOptions += "-feature"

scalacOptions += "-language:reflectiveCalls"

scalacOptions += "-language:postfixOps"
