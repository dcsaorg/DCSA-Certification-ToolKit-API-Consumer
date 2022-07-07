FROM eclipse-temurin:17-jre-alpine
COPY run-in-container.sh /run.sh
RUN chmod +x /run.sh
COPY src/main/resources/application.yaml .
COPY target/dcsa_ctk_consumer-*.jar .
CMD ["/run.sh"]