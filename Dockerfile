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
# Expone el puerto típico de CORBA, puedes cambiarlo si tu servidor usa otro
EXPOSE 1050
# Copia los archivos compilados
COPY --from=build /app/target/classes/ /app/classes/
COPY --from=build /app/target/idl/ /app/idl/
# Copia recursos si los hay
COPY --from=build /app/target/resources/ /app/resources/
# Establece el directorio de trabajo
WORKDIR /app
# Ejecuta el servidor CORBA principal (ajusta el nombre de la clase si es necesario)
ENTRYPOINT ["java", "-cp", "/app/classes:/app/idl:/app/resources", "Server.BufferServer"]