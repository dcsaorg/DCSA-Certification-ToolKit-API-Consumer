# DCSA-Certification-ToolKit-API-Consumer

Status
-------------------------------------
This certification toolkit is targeted at the DCSA standard for Track and Trace v2.2.

This certification toolkit is currently in a "pre-alpha" development stage. It must not be used to establish conformance of implementations of the standard.

It is made available in order to allow early feedback on the toolkit itself.


Building and Running the project,
-------------------------------------

If you would like to build required DCSA packages individually, begin with step 1.

1) Build **DCSA-TNT** as described in [DCSA-TNT/README.md](https://github.com/dcsaorg/DCSA-TNT/blob/b7d1cbde029c3e6dbe8c7ca71f1fcf36b30f9b65/README.md) 
   For TNT 2.2 we have to checkout tag 0.0.1-reactive ``git checkout tags/0.0.1-reactive``
   Build it as a library by commenting out the build plugin in the pom.xml. 

2) Clone **DCSA-Certification-ToolKit-API-Consumer** (with ``--recurse-submodules`` option.)

3) Build using ``mvn clean install``

4) Run application
```
mvn spring-boot:run [options] 

options:
 -Dspring-boot.run.arguments="--DB_HOSTNAME=localhost:5432 --AUTH0_ENABLED=false --LOG_LEVEL=DEBUG"
```

OR using **docker-compose**

```
docker-compose up -d -V --build
```
**Publish the docker images using github package**

Along with Consumer conformance toolkit, we need a running database intance loaded with test data. Hence, we need below two docker images to publish.

Assuming you have docker images availabe at your local and github repo created
1. Consumer Compatibility kit: 
```
docker tag <image_name >:latest docker.pkg.github.com/<user_name>/<repo_name>/ctk-consumer:<tagname>
docker push docker.pkg.github.com/<user_name>/<repo_name>/ctk-consumer:<tagname>
```

2. Reference database
```
docker tag <image_name>:latest docker.pkg.github.com/<user_name>/<repo_name>/ctk-db:<tagname>
docker push docker.pkg.github.com/<user_name>/<repo_name>/ctk-db:<tagname>
```

Consumer CTK Extension
-------------------------------------

We need a way to generate test shipment events. This test event time should be configurable event time in the past, present, and future. 
That is why this new extension was implemented.

| Endpoints for this extension                     | 
|--------------------------------------------------| 
| POST /conformance/data-management/uploadShipment | 
| GET /conformance/data-management/example-data/full-shipment |
| GET /conformance/data-management/example-data/full-shipment?timeOffset=plus4d (with time offset) |
| DELETE /conformance/data-management/removeLastShipment |
| DELETE /conformance/data-management/removeAllEvent |

<mark>Note that the above endpoint won't work out of the box. Because the DCSA information model changes frequently. We have to update according to the updates</mark>

Powershell script for generating and loading test data. Primarily used with conformance tooling.

It will find all JSON file recursively and perform POST request from  current directory   
`.\upload.ps1`

Pass the json file as parameter (-Param1 jsonFullShipment.json)   
`.\uploadShipmentEvent.ps1 -Param1 jsonFullShipment.json`

Users can upload shipment event either by these powershell script or calling above rest endpoints by postman.

Referenc: 

https://faun.pub/how-to-setup-your-first-github-packages-docker-repository-2cda078e6836


