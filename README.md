# DCSA-Certification-ToolKit-API-Consumer

Building and Running the project,
-------------------------------------

If you would like to build required DCSA packages individually, begin with step 1.

1) Build **DCSA-TNT** as described in [DCSA-TNT/README.md](https://github.com/dcsaorg/DCSA-TNT/blob/b7d1cbde029c3e6dbe8c7ca71f1fcf36b30f9b65/README.md), then

2) Clone **DCSA-Certification-ToolKit-API-Consumer** (with ``--recurse-submodules`` option.)

3) update **DCSA-TNT** as dependency in pom file and Build using, ``mvn package``

4) Initialize your local postgresql database as described in [datamodel/README.md](https://github.com/dcsaorg/DCSA-Information-Model/blob/master/README.md) \
   or If you have docker installed, you may skip this step and use the docker-compose command mentioned below to set it up (This will initialize the application along with the database).

5) Run application,
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

Along with Consumer Compatibility kit, we need a running database intance loaded with test data. Hence, we need below two docker images to publish.

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


We have already a repo for this, we can cintinue to use the same 
https://github.com/deepakdcsa/ctk-docker-registry.git

Referenc: 

https://faun.pub/how-to-setup-your-first-github-packages-docker-repository-2cda078e6836


