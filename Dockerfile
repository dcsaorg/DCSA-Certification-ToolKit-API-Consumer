FROM debian:buster

RUN apt-get update \
    && DEBIAN_FRONTEND=noninteractive apt-get -y upgrade \
    && DEBIAN_FRONTEND=noninteractive apt-get -y install --no-install-recommends \
        openjdk-11-jre-headless \
    && rm -rf /var/lib/apt/lists/*
RUN mkdir -p /ctk
COPY target/dcsa_ctk_consumer-*.jar /ctk/dcsa_ctk_consumer.jar
WORKDIR /ctk/
ENTRYPOINT java -jar dcsa_ctk_consumer.jar