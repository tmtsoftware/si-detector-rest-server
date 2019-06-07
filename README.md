# Play REST API for Galil HCD

This is an example project based on [Making a REST API in Play](http://developer.lightbend.com/guides/play-rest-api/index.html) 
that shows how you could make a REST API for the Galil prototype HCD using the Play Framework.

This project depends on `galil-prototype` and `csw-prod`. Be sure to run `sbt publishLocal stage` in those projects first.

### Running

This play server assumes that the csw-prod location service (csw-cluster-seed), 
galil simulator (or local device) and galil-hcd are running.
For testing, you can run these commands in separate shell tabs.

First set the environment variables (Replace interface name, IP address and port with your own values):

```bash
export interfaceName=enp0s31f6
export clusterSeeds=192.168.178.77:7777
```
or 

```csh
setenv interfaceName enp0s31f6
setenv clusterSeeds 192.168.178.77:7777
```

Start the location service: 

```
csw-cluster-seed --clusterPort 7777
```

Start the galil-simulator (unless connecting to the device itself)

```
galil-simulator
```

Start the galil HCD:

```
galil-hcd
```
or 

```
galil-hcd -Dgalil.host=galilhost -Dgalil.port=23
```

You can run the server with sbt:

    sbt run

or after running `sbt stage`, with:

    ./target/universal/stage/bin/galil-play-rest-api

Play will start up on the HTTP port at http://localhost:9000/.   

### Usage

If you send this URL from the command line, you’ll see the JSON result. 
Using httpie, you can execute the command:

```
http POST 'http://localhost:9000/v1/gs/setRelTarget?axis=A&count=2'
```

and get back something like:

```
"Completed(RunId(673dc403-7184-4b9d-9eca-3e327c5e14ab))"
```

Or:

```
http GET 'http://localhost:9000/v1/gs/getRelTarget?axis=A'
```

returns:

```
"CompletedWithResult(RunId(18d51eeb-8863-48cf-8e37-86716e3d74a5),Result([TEST, test.galil.server](counts((2)NoUnits))))"
```

### In Progress...

Only a few Galil commands are implemented so far...

An API for accessing the Galil DataRecord via the HCD and REST API still needs to be added.

It might be simpler to implement this using akka-http. Maybe a second project will be started to try that.


