# Use a imagem de build padrão do Quarkus (não precisa de GraalVM)
FROM eclipse-temurin:25-jdk AS build
WORKDIR /code
COPY . .
# Build normal (JAR), não nativo
RUN ./gradlew build -Dquarkus.package.type=fast-jar

# Imagem de execução leve
FROM eclipse-temurin:25-jre
WORKDIR /work/
COPY --from=build /code/build/quarkus-app/ /work/
EXPOSE 8080
CMD ["java", "-jar", "/work/quarkus-run.jar"]