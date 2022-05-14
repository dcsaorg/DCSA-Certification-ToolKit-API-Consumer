FROM eclipse-temurin:17-jre-alpine
RUN mkdir -p /ctk
COPY target/dcsa_ctk_consumer-*.jar /ctk/dcsa_consumer_ctk.jar
WORKDIR /ctk/
ENTRYPOINT java -jar dcsa_consumer_ctk.jar