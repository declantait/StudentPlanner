
name := """StudentPlanner"""
organization := "B00270382"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

lazy val myProject = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.12.4"

libraryDependencies += guice

// Enables database connection plugin
libraryDependencies += javaJdbc
libraryDependencies += jdbc
libraryDependencies += "com.h2database" % "h2" % "1.4.196"

// https://mvnrepository.com/artifact/javax.xml.bind/jaxb-api
// fixes error where app would crash upon page reload due to java sdk version
libraryDependencies += "javax.xml.bind" % "jaxb-api" % "2.1"

libraryDependencies += "org.awaitility" % "awaitility" % "2.0.0" % Test
libraryDependencies += "org.assertj" % "assertj-core" % "3.6.2" % Test
libraryDependencies += "org.mockito" % "mockito-core" % "2.1.0" % Test