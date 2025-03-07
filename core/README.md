# Core

<!-- TOC depthfrom:2 -->

- [:notebook\_with\_decorative\_cover: Description](#notebook_with_decorative_cover-description)
- [:building\_construction: Building](#building_construction-building)
  - [Building JAR file](#building-jar-file)
  - [Building documentation](#building-documentation)
- [:package: Publishing](#package-publishing)
  - [Publishing GitHub package](#publishing-github-package)
- [:computer\_mouse: How to use](#computer_mouse-how-to-use)

<!-- /TOC -->

## :notebook_with_decorative_cover: Description

Java component containg the domain of the Persons with Reduced Mobility Problem and utilities to manage aspects as input and output. It is the core component to address the problem by other software components.

## :building_construction: Building

> :warning: This software depends on packages published privately in GitHub Packages. It is required to configure Maven as indicated [here](https://github.com/kaizten/kaizten-base/blob/main/java/instructions-access-github-packages.md).

### Building JAR file

The main distribution of the component is a [JAR file](https://docs.oracle.com/javase/8/docs/technotes/guides/jar/jarGuide.html). This can be created as follows:
```shell
$ mvn package
```
The JAR is generated in the folder `target` of the component. By default, the resulting JAR file is `target/core-1.0-SNAPSHOT.jar`.

### Building documentation

The documentation of the component in [JavaDoc](https://www.oracle.com/es/java/technologies/javase/javadoc-tool.html#javadocdocuments) can be generated as follows:
```shell
$ mvn javadoc:javadoc
```
The documentation is generated in the folder `target/site` of the project. In particular, the file `index.html` is the main HTML file of the documentation generated.

## :package: Publishing

### Publishing GitHub package

The component is published as [GitHub package](https://docs.github.com/en/packages/learn-github-packages/introduction-to-github-packages). This is done as follows:
```shell
$ mvn -Pgithub deploy
```
The resulting package is published [here](https://github.com/orgs/kaizten/packages?repo_name=persons-reduced-mobility-problem).

## :computer_mouse: How to use

> :warning: This software is published privately in GitHub Packages. It is required to configure Maven as indicated [here](https://github.com/kaizten/kaizten-base/blob/main/java/instructions-access-github-packages.md).

The component can be included in a [Maven](https://maven.apache.org) project as dependency. This is done as follows:
```xml
<dependency>
    <groupId>com.kaizten.prmp</groupId>
    <artifactId>core</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```