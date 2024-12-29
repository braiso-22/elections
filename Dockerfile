# Creamos una nueva imagen a partir de openjdk:17-jdk-slim
FROM openjdk:17-jdk-slim

COPY . /app

# Nos movemos a la carpeta /app
WORKDIR /app
# Ejecutamos el comando ./gradlew distTar
RUN ./gradlew distTar

# Creamos un directorio /kotlin dentro de nuestra imagen
RUN mkdir /kotlin

# Descomprimimos en la carpeta /kotlin que creamos arriba, el tar que generamos previamenre con ./gradlew distTar
RUN tar -xvf /app/build/distributions/elections-0.0.1.tar -C /kotlin

# WORKDIR es como hace un comando cd, es decir, nos pasamos a /kotlin/app
WORKDIR /kotlin/elections-0.0.1

# Ejecutamos la app
CMD ["bin/elections"]