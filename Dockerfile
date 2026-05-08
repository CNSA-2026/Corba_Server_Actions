FROM maven:3.9.7-eclipse-temurin-8 AS build

WORKDIR /app

# First copy only the pom file. This is the file with less change
COPY ./pom.xml .

# Download the package and make dependencies cached in docker image
RUN mvn -B -f ./pom.xml -s /usr/share/maven/ref/settings-docker.xml clean dependency:go-offline

# Copy the actual code
COPY ./ .

# Then build the code - On Windows
#RUN mvn -B -f ./pom.xml -s /usr/share/maven/ref/settings-docker.xml clean spring-javaformat:apply --no-transfer-progress
#RUN mvn -B -f ./pom.xml -s /usr/share/maven/ref/settings-docker.xml package --no-transfer-progress

# Then build the code - on Linux
RUN mvn -B -f ./pom.xml -s /usr/share/maven/ref/settings-docker.xml clean package 

# Start with a base image containing Java 8 runtime
FROM eclipse-temurin:8-jre
# Expone el puerto típico de CORBA
EXPOSE 1050 3000
# Copia los archivos compilados
COPY --from=build /app/target/classes/ /app/classes/
# Copia el JAR con dependencias para que esté disponible si se necesita
COPY --from=build /app/target/corba-buffer-server-1.0.0-jar-with-dependencies.jar /app/corba-server.jar
# Establece el directorio de trabajo
WORKDIR /app
# Script para ejecutar el servidor CORBA con argumentos ORB
RUN echo '#!/bin/sh' > /app/start-server.sh && \
    echo 'exec java -cp /app/classes:/app/corba-server.jar Server.BufferServer "$@"' >> /app/start-server.sh && \
    chmod +x /app/start-server.sh
# Ejecuta el servidor CORBA
ENTRYPOINT ["/app/start-server.sh"]