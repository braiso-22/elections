# Etapa de build: compilamos el módulo :server del proyecto KMP
FROM eclipse-temurin:21-jdk AS build

COPY . /app

# Nos movemos a la carpeta /app
WORKDIR /app
# Generamos la distribución del servidor en server/build/install/server
RUN ./gradlew :server:installDist --no-daemon

# Etapa de ejecución: solo copiamos la distribución generada
FROM eclipse-temurin:21-jre

COPY --from=build /app/server/build/install/server /kotlin/server

# WORKDIR es como hace un comando cd, es decir, nos pasamos a /kotlin/server
WORKDIR /kotlin/server

EXPOSE 8080

# Ejecutamos la app
CMD ["bin/server"]
