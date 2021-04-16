# Run ./gradlew assemble first
FROM ghcr.io/graalvm/graalvm-ce:java11-21.0.0.2 AS graalvm
RUN gu install native-image
WORKDIR /home/app
COPY build/layers/libs /home/app/libs
COPY build/layers/resources /home/app/resources
COPY build/layers/application.jar /home/app/application.jar
RUN native-image -H:IncludeResources='.*sql$|.*xml$|.*yml$' -H:Class=catalogue.Application -H:Name=application --no-fallback -cp /home/app/libs/*.jar:/home/app/resources:/home/app/application.jar
FROM oraclelinux:8-slim
COPY --from=graalvm /home/app/application /app/application
ENTRYPOINT ["/app/application", "-Dmicronaut.server.port=80"]
EXPOSE 80