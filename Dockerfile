FROM eclipse-temurin:17-jre-alpine
RUN mkdir -p /ctk
RUN mkdir -p /ctk/config/uploaded/events/v2
COPY config/uploaded/application.yml /ctk/config/uploaded/application.yml
COPY config/uploaded/events/v2 /ctk/config/uploaded/events/v2
COPY run-in-container.sh /ctk/run.sh
RUN chmod +x /ctk/run.sh
COPY src/main/resources/application.yml /ctk
COPY target/dcsa_ctk_consumer-*.jar /ctk
WORKDIR /ctk
CMD ["/ctk/run.sh"]
#ENTRYPOINT ["tail", "-f", "/dev/null"]