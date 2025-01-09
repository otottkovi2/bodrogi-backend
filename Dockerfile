FROM amazoncorretto:21 AS build
COPY ./src /data/src
COPY ./gradlew /data/gradlew
COPY ./gradle/ /data/gradle/
COPY ./*.gradle.kts /data/
WORKDIR /data
RUN chmod +x ./gradlew
RUN ./gradlew build -x test

FROM amazoncorretto:21 AS run
COPY --from=build /data/build/libs/backend-*.jar /data/backend.jar
WORKDIR /data
EXPOSE 8080
CMD ["java","-jar","backend.jar"]
