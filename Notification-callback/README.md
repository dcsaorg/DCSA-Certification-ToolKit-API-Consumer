# External notification callback service 

This is a simple notification callback spring boot app that listens on port 9990. That exposes the HEAD and POST endpoints just to simulate the TNT event subscription's callback. It was an older version. Later on, we develop the same using Postman's mock server.

https://f12d95eb-6e7b-4516-ac10-7c3c9efe2887.mock.pstmn.io/456eacf9-8cda-412b-b801-4a41be7a6d000

This mock server exposes 3 endpoints
GET, HEAD and POST

Building and Running the project,
-------------------------------------

```
mvn clean install
mvn spring-boot:run [options] 
```



