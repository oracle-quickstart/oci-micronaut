FROM ghcr.io/graalvm/graalvm-ce:ol8-java11-21.1.0
WORKDIR /home/app
COPY classes /home/app/classes
COPY dependency/* /home/app/libs/
EXPOSE 8080
RUN microdnf install strace
ENTRYPOINT ["strace", "-f", "java", "-cp", "/home/app/libs/*:/home/app/classes/", "mushop.carts.Application"]
