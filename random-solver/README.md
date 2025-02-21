# Random solver

<!-- TOC -->

- [:notebook\_with\_decorative\_cover: Description](#notebook_with_decorative_cover-description)
- [:building\_construction: Building](#building_construction-building)
  - [Building JAR file](#building-jar-file)
    - [Building JAR with all dependencies](#building-jar-with-all-dependencies)
  - [Building Docker image](#building-docker-image)
  - [Building documentation](#building-documentation)
- [:package: Publishing](#package-publishing)
  - [Publishing Docker image](#publishing-docker-image)
- [:gear: Running](#gear-running)
  - [Running from source code](#running-from-source-code)
  - [Running JAR file with all dependencies](#running-jar-file-with-all-dependencies)
  - [Running as Docker container](#running-as-docker-container)
- [:computer\_mouse: How to use](#computer_mouse-how-to-use)
  - [Help](#help)
  - [Example of usage](#example-of-usage)

<!-- /TOC -->
## :notebook_with_decorative_cover: Description

Software aimed at validating problem instances of the Production Planning Problem. The format of the instances is described [here](https://github.com/kaizten/production-planning#problem-instances). The software uses the JSON Schema of the problem instances to validate. It is available [here](https://github.com/kaizten/production-planning/blob/main/json-schema/production-planning-problem_schema.json).

## :building_construction: Building

> :warning: This software depends on packages published privately in GitHub Packages. It is required to configure Maven as indicated [here](https://github.com/kaizten/kaizten-base/blob/main/java/instructions-access-github-packages.md).

### Building JAR file

The main distribution of the component is a [JAR file](https://docs.oracle.com/javase/8/docs/technotes/guides/jar/jarGuide.html). This can be created as follows:
```shell
$ mvn package
```
The JAR is generated in the folder `target` of the component. By default, the resulting JAR file is `target/instance-validator-1.0-SNAPSHOT.jar`.

#### Building JAR with all dependencies

The following command must be executed to compile and assemble the project with all the dependencies packaged:
```shell
$ mvn compile assembly:single
```
The JAR is generated in the folder `target` of the component. By default, the resulting JAR file is `target/instance-validator-1.0-SNAPSHOT-jar-with-dependencies.jar`.

### Building Docker image

> :warning: In order to create a [Docker image](https://en.wikipedia.org/wiki/Docker_(software)) of the component, its JAR file with all dependencies must be firstly available. This JAR file can be created as indicated [here](#building-jar-with-all-dependencies).

A [Docker image](https://en.wikipedia.org/wiki/Docker_(software)) can be created for the component, as follows:
```shell
$ docker build -t kaizten/production-planning-problem_instance-validator .
```
The resulting [Docker image](https://en.wikipedia.org/wiki/Docker_(software)) is named `kaizten/production-planning-problem_instance-validator:latest` and located in the default Docker storage in the host machine. It should be noted that any other name can be used. Also, an optional tag can be used to create a specific version or variant of the image. More information about tags can be found [here](https://docs.docker.com/engine/reference/commandline/tag/).

### Building documentation

The documentation of the component in [JavaDoc](https://www.oracle.com/es/java/technologies/javase/javadoc-tool.html#javadocdocuments) can be generated as follows:
```shell
$ mvn javadoc:javadoc
```
The documentation is generated in the folder `target/site` of the project. In particular, the file `index.html` is the main HTML file of the documentation generated.

## :package: Publishing

### Publishing Docker image

> :warning: The [Docker image](https://en.wikipedia.org/wiki/Docker_(software)) of the component must be available to be published. This image can be created as indicated [here](#building-docker-image).

The component is published as [Docker image](https://en.wikipedia.org/wiki/Docker_(software)) in [DockerHub](https://hub.docker.com). This is done as follows:
```shell
$ docker login
> Username: <INTRODUCE USERNAME>
> Password: <INTRODUCE PASSWORD>
$ docker push kaizten/production-planning-problem_instance-validator:latest
```
It is worth mentioning that `username` and `password` of [Kaizten account in DockerHub](https://hub.docker.com/u/kaizten) are required when not logged by `docker` previously. The resulting [Docker image](https://en.wikipedia.org/wiki/Docker_(software)) is published [here](https://hub.docker.com/u/kaizten).

## :gear: Running

### Running from source code

The component can be executed from its source code as follows:
```shell
$ mvn exec:java -Dexec.mainClass=com.kaizten.ppp.validator.Main -Dexec.args=[options]
```
In this case, `[options]` represents the options that can be used as input in the component. See [help](#help) for more information.

### Running JAR file with all dependencies

> :warning: The JAR file with all dependencies must be available to be run. This JAR file can be created as indicated [here](#building-jar-with-all-dependencies).

The JAR file can executed as follows:
```shell
$ java -jar target/instance-validator-1.0-SNAPSHOT-jar-with-dependencies.jar [options]
```
In this case, `[options]` represents the options that can be used as input in the component. See [help](#help) for more information.

### Running as Docker container

> :warning: In order to execute the component as [Docker container](https://www.docker.com/resources/what-container/#:~:text=A%20Docker%20container%20image%20is,tools%2C%20system%20libraries%20and%20settings.), the [Docker image](https://en.wikipedia.org/wiki/Docker_(software))  must be firstly available. This [Docker image](https://en.wikipedia.org/wiki/Docker_(software)) can be created as indicated [here](#building-docker-image).

The component can be run as [Docker container](https://www.docker.com/resources/what-container/#:~:text=A%20Docker%20container%20image%20is,tools%2C%20system%20libraries%20and%20settings.) as follows:
```shell
$ docker run kaizten/production-planning-problem_instance-validator [options]
```
In this case, that default image tagged with `latest` is used. However, the optional `tag` can be used to specify other version of the [Docker image](https://en.wikipedia.org/wiki/Docker_(software)). More information can be found [here](https://docs.docker.com/engine/reference/commandline/tag/). Furthermore, `[options]` represents the options that can be used as input in the component. See [help](#help) for more information.

## :computer_mouse: How to use

### Help

```shell
Usage: Instance validator of the Production planning problem [options]
  Options:
    -h, --help     Prints this help and exists
  * -i, --instance URI of the problem instance
```

### Example of usage

In the following example, a problem instance `instance-test.json` is validated against the JSON Schema of the problem:
```shell
$ docker run -v ~/problem-instances:/data kaizten/production-planning-problem_instance-validator -i file:/data/instance-test.json
{"ERROR": [
     "(required): $.references: is missing but it is required",
     "(required): $.orders: is missing but it is required",
     "(required): $.staff: is missing but it is required",
     "(required): $.lines: is missing but it is required",
     "(required): $.planningHorizon: is missing but it is required"
]}
```
It should be noted that volume `data` is mounted in local folder `~/problem-instances` in the example to use local files.