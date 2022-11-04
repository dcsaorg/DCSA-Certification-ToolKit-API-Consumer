FROM eclipse-temurin:17-jre-alpine
RUN mkdir -p /ctk
RUN mkdir -p /ctk/config/uploaded/events/v2
COPY config/uploaded/application.yml /ctk/config/uploaded/application.yml
COPY config/uploaded/events/v2 /ctk/config/uploaded/events/v2
COPY src/main/resources/application.yml /ctk/
COPY target/dcsa_ctk_consumer-*.jar /ctk/
WORKDIR /ctk
CMD ["java","-jar","/ctk/dcsa_ctk_consumer-0.0.1.jar"]
